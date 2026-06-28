#!/bin/bash
# VERIFICATION SCRIPT - Run this to verify all files are in place

echo "======================================"
echo "SMS Backend Error Handling System"
echo "File Verification Script"
echo "======================================"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Counters
FOUND=0
MISSING=0

# Function to check file
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
        ((FOUND++))
    else
        echo -e "${RED}✗${NC} $1 (MISSING)"
        ((MISSING++))
    fi
}

# Function to check directory
check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/"
        ((FOUND++))
    else
        echo -e "${RED}✗${NC} $1/ (MISSING)"
        ((MISSING++))
    fi
}

echo "=== DOCUMENTATION FILES ==="
check_file "INDEX.md"
check_file "README_ERROR_HANDLING.md"
check_file "ERROR_HANDLING_GUIDE.md"
check_file "ERROR_HANDLING_QUICK_REFERENCE.md"
check_file "IMPLEMENTATION_CHECKLIST.md"
check_file "MIGRATION_GUIDE.md"
check_file "DELIVERY_SUMMARY.md"
echo ""

echo "=== EXCEPTION CLASSES ==="
check_file "sms-backend/src/main/java/com/smartschool/api/exception/ApiError.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/CustomException.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/ResourceNotFoundException.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/ValidationException.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/UnauthorizedException.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/ForbiddenException.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/DuplicateResourceException.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/FileUploadException.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/DataIntegrityException.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/DatabaseException.java"
echo ""

echo "=== CORE INFRASTRUCTURE ==="
check_file "sms-backend/src/main/java/com/smartschool/api/exception/ErrorConstants.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/ErrorUtil.java"
check_file "sms-backend/src/main/java/com/smartschool/api/exception/GlobalExceptionHandler.java"
check_file "sms-backend/src/main/java/com/smartschool/api/util/ApiResponse.java"
echo ""

echo "=== EXAMPLE IMPLEMENTATIONS ==="
check_file "sms-backend/src/main/java/com/smartschool/api/example/ExampleControllerWithErrorHandling.java"
check_file "sms-backend/src/main/java/com/smartschool/api/example/ExampleServiceWithErrorHandling.java"
check_file "sms-backend/src/main/java/com/smartschool/api/example/ErrorHandlingIntegrationTests.java"
echo ""

echo "======================================"
echo "Summary:"
echo -e "${GREEN}✓ Found: $FOUND files${NC}"
if [ $MISSING -gt 0 ]; then
    echo -e "${RED}✗ Missing: $MISSING files${NC}"
else
    echo -e "${GREEN}✓ All files present!${NC}"
fi
echo "======================================"
echo ""

if [ $MISSING -eq 0 ]; then
    echo "Status: ✅ COMPLETE - Ready for implementation"
    exit 0
else
    echo "Status: ⚠️ INCOMPLETE - Some files are missing"
    exit 1
fi

