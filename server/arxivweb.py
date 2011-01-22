import web
import gzip
import urllib
import os
import os.path
import tarfile
import re
from urlparse import parse_qs

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

    return filename

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
      os.rename(filename, filename[:-4] + ".tex")
      return
    tar_file = tarfile.open(filename, 'r')
    print "Extracting " + filename + " to " + os.path.dirname(filename)
    tar_file.extractall(os.path.dirname(filename))
    tar_file.close()


if __name__ == "__main__": app.run()
