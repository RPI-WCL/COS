# Release Script for IO

echo "Creating files for IO release "$1

mkdir release-work-tmp
cd release-work-tmp
echo "Checking out top of cvs module Io. Please wait."
cvs -Q checkout Io
echo "Removing CVS directories."
rm -rf `find ./ | grep "CVS$"`

echo "Creating io"$1"-src.tar.gz"
tar -czf "../io"$1"src.tar.gz" Io

echo "Compiling"

java salsac.SalsaCompiler `find ./ | grep "salsa$"`
javac `find ./ | grep "java$"`

cd Io
echo "Creating io"$1".jar"
jar -cf "../../io"$1".jar" `find ./ | grep "class$"`

echo "Cleaning up"
cd ../../
rm -rf release-work-tmp
echo "Files are in the CWD"
