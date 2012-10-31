import os
import stat
import subprocess

def dirWalker():
    files = os.listdir('.')
    for x in files:
        if '.git' in x:
            continue
        if os.path.isdir(x):
            os.chdir(x)
            dirWalker()
            os.chdir('..')
        elif x[-5:] == ".java":
            print "Compiling", x + "..."
            subprocess.call("javac " + x, shell=True)

dirWalker()
