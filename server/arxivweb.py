import web
import gzip
import urllib
import os
import os.path
import sys
import tarfile
import re
from urlparse import parse_qs

import latex_sanitizer

urls = (
    '/download(.*)', 'download_paper'
    )

app = web.application(urls, globals())

class download_paper:
  
  def findRefType(self, ref):
    ref = ref.replace('arxiv:','')
    if re.search(r'^[a-zA-Z\-]+/\d{7}$',ref):
      type = 'old-style eprint'
    elif re.search(r'^\d{7}$',ref):
      type = 'old-style eprint'
      ref = 'hep-th/' + ref
    elif re.search('^\d{4}\.\d{4}$',ref):
      type = 'new-style eprint'
    else:
      type = 'not arXiv'

    return type, ref
  
  def GET(self, name):
    arxiv_id = (parse_qs(web.ctx.query[1:]))['arxiv_id'][0]
    type, ref = self.findRefType(arxiv_id)
    filename = self.download_source(ref, type, "docs/")
    self.untar_source(filename)
    tex_file_name = self.determine_main_tex_file(filename)
    if tex_file_name is None:
      print >> sys.stderr, "There was no document present in the .tar"
      return "ERROR"

    f = open(tex_file_name, 'r')
    tex_file = f.read()
    f.close()
    
    os.rename(tex_file_name, tex_file_name + '.orig')
    
    tex_file = self.sanitize(tex_file)

    f = open(tex_file_name, 'w')
    f.write(tex_file)
    f.close()

    pdf_filename = self.generate_pdf(tex_file_name)
    
    if pdf_filename:
      self.convert_pdf(pdf_filename, os.path.join('/mnt/xindle-docs/', ref.replace('/', '-')))
      return 'http://xindle-docs.s3.amazonaws.com/%s/%s-page-0.png' % (ref.replace('/', '-'), ref.replace('/', '-'))
    else:
      return "ERROR"

  def download_source(self, ref, type, download_path):
    download_path = os.path.expanduser(download_path)
    try:
      os.mkdir(download_path + ref.replace('/', '-'))
    except OSError:
      print "Directory " + download_path + ref.replace('/', '-') + " already exists!"
    download_path = download_path + ref.replace('/', '-') + "/"
    filename = download_path + ref.replace('/', '-') + ".tar"
    urllib.urlretrieve('http://arxiv.org/e-print/' + ref, filename + ".dum")
    gzip_file = gzip.GzipFile(filename + ".dum")
    source_file = open(filename, 'w')
    source_file.write(gzip_file.read())
    source_file.close()
    gzip_file.close()
    os.remove(filename + ".dum")
    return filename

  def untar_source(self, filename):
    if not tarfile.is_tarfile(filename):
      print filename + " is not a tar file. No need to untar."
      os.rename(filename, filename[:-4] + '.tex')
      return
    tar_file = tarfile.open(filename, 'r')
    print "Extracting " + filename + " to " + os.path.dirname(filename)
    tar_file.extractall(os.path.dirname(filename))
    tar_file.close()

  def determine_main_tex_file(self, filename):
    texdir = os.path.dirname(filename)
    for fn in os.listdir(texdir):
      f = open(os.path.join(texdir, fn), 'r')
      fc = f.read()
      f.close()
      if re.search(r'\\begin\{document\}', fc):
        return os.path.join(texdir, fn)
    return None

  def sanitize(self, tex_file):
    tex_file = latex_sanitizer.shrink_margins(tex_file)
    tex_file = latex_sanitizer.remove_links(tex_file)
    return tex_file

  def generate_pdf(self, filename):
    rc = os.system('pdflatex -halt-on-error -output-directory %s %s' % (os.path.dirname(filename), filename))
    if rc == 0:
      rc = os.system('pdflatex -halt-on-error -output-directory %s %s' % (os.path.dirname(filename), filename))
    if rc != 0:
      print >> sys.stderr, "ERROR: Generating PDF file for %s" % filename
      return None
    return filename[:-4] + '.pdf'

  def convert_pdf(self, pdf_filename, dst_path):
    print >> sys.stderr, "Beginning conversion of %s" % pdf_filename
    os.system("convert -verbose -density 200 %s -scale 1000 %s" % (pdf_filename, pdf_filename[:-4] + '-page.png'))
    os.system("mkdir %s" % dst_path)
    os.system("mv %s %s" % (os.path.join(os.path.dirname(pdf_filename), '*-page-*.png'), dst_path))
    
    print >> sys.stderr, "Done conversion of %s" % pdf_filename

if __name__ == "__main__": app.run()
