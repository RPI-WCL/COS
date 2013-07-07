export CLASSPATH=./classes:$CLASSPATH 
DIST=./classes
VERSION=0.4

echo "IOS Build Script"
echo "Please make sure the current directory is in your CLASSPATH"
echo ""

if [ -d $DIST ]; then
		rm -rf $DIST
fi
echo "Making dir: "$DIST
mkdir $DIST
echo ""

echo "Compiling salsa source..."
java -DXsilent salsac.SalsaCompiler `find src/ | grep ".salsa$"`
echo "java source..."
javac -Xlint:none -d $DIST `find src/ | grep ".java$"`

echo "Generating jar file..."
cd $DIST
jar cf ../ios$VERSION.jar `find src/ | grep ".class$"`
cd ..

echo ""
echo "Finished!"

