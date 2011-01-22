import re
import sys

def has_package(tex_file, package_name):
  if re.search(r'\\usepackage(\[.*\])?\{%s\}' % package_name, tex_file):
    return True
  return False

def add_package(tex_file, package_name, package_args = []):
  if has_package(tex_file, package_name):
    print >> sys.stderr, "Already contains package %s!" % package_name
    return tex_file
  p = re.finditer(r'\\usepackage(\[.*\])?\{.*\}', tex_file)
  last_package = -1
  for match in p:
    last_package = match.span()[1]
  if last_package < 0:
    print >> sys.stderr, "ERROR: No \usepackage directives... where do I put it?"
    return tex_file
  new_tex_file = tex_file[:last_package] + ("\n\\usepackage%s{%s}\n" % (('[%s]' % (','.join(package_args) if (len(package_args) > 0) else '')), package_name)) + tex_file[last_package:]
  return new_tex_file

def remove_package(tex_file, package_name):
  if not has_package(tex_file, package_name):
    print >> sys.stderr, "Nothing to remove, does not contain package %s!" % package_name
    return tex_file
  return re.subn(r'\\usepackage(\[.*\])?\{%s\}' % package_name, '\n%% Removed package %s\n' % package_name, tex_file)[0]


def shrink_margins(tex_file):
  tex_file = remove_package(tex_file, 'fullpage')
  tex_file = remove_package(tex_file, 'geometry')
  tex_file = add_package(tex_file, 'geometry', ['left=10pt', 'right=10pt', 'bottom=10pt', 'top=10pt'])
  return tex_file

# Testing purposes only
# f = open('/home/adrian/arxiv/docs/1101.3686/1101.3686.tex', 'r')
# tex_file = f.read()
# f.close()
# print shrink_margins(tex_file)
