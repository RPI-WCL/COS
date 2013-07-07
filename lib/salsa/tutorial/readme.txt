To compile the tutorial:
  latex rpithes
  bibtex rpithes
  latex rpithes
  latex rpithes
  dvipdf rpithes.dvi salsa.pdf

to generate the html files:
  latex2html rpithes
  cd rpithes
  replace <file:..../icon> "icon" -- *
    ** where <file:..../icon> is the wrong generated icon path
    ** you need to take a look at one of the html files to decide it
  generate images for the html files  