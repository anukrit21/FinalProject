#!/bin/bash

# Color definitions
BLUE='\033[0;34m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}DemoApp Package Name Standardization Script${NC}"
echo "----------------------------------------"

# Define patterns to replace
PATTERN_LOWER="com.demoapp."
PATTERN_CORRECT="com.demoApp."
GROUP_ID_LOWER="<groupId>com.demoapp</groupId>"
GROUP_ID_CORRECT="<groupId>com.demoApp</groupId>"

echo -e "${BLUE}This script will update package names and Maven groupIds to use the correct format: ${PATTERN_CORRECT}${NC}"
echo -e "${RED}Warning: This operation will modify source files. Make sure you have a backup.${NC}"
echo ""

# Ask for confirmation
read -p "Do you want to continue? (y/n): " CONFIRM
if [[ ! "$CONFIRM" =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}Operation cancelled.${NC}"
    exit 0
fi

echo ""
echo -e "${BLUE}Scanning for Java files...${NC}"

# Create a temporary directory for storing backup files
mkdir -p backup/java
mkdir -p backup/xml

# Find all Java files with the incorrect package name pattern and process them
find . -type f -name "*.java" -exec grep -l "package $PATTERN_LOWER" {} \; | while read file; do
    # Create a backup
    cp "$file" "backup/java/$(basename "$file")"
    
    # Replace the package name
    sed -i.bak "s/package $PATTERN_LOWER/package $PATTERN_CORRECT/g" "$file"
    
    # Remove the backup file created by sed
    rm -f "$file.bak"
    
    echo "Updated Java file: $file"
done

echo ""
echo -e "${BLUE}Scanning for Maven pom.xml files...${NC}"

# Find all pom.xml files with the incorrect groupId pattern and process them
timestamp=$(date +%Y%m%d%H%M%S)
find . -type f -name "pom.xml" -exec grep -l "$GROUP_ID_LOWER" {} \; | while read file; do
    # Create a backup
    cp "$file" "backup/xml/$(basename "$file").$timestamp"
    
    # Replace the groupId
    sed -i.bak "s/$GROUP_ID_LOWER/$GROUP_ID_CORRECT/g" "$file"
    
    # Remove the backup file created by sed
    rm -f "$file.bak"
    
    echo "Updated XML file: $file"
done

echo ""
echo -e "${GREEN}Package name standardization completed!${NC}"
echo "Original files have been backed up to the 'backup' directory."
echo ""
echo "Note: You may need to rebuild your project to apply the changes." 