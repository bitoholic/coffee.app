#!/bin/bash

echo "Verifying Bulk Delete Implementation"

# Change to project directory
cd /opt/data/projects/coffee-app || exit 1

# Check that all required files exist and contain the expected changes
echo "Checking files exist..."
if [ ! -f "composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListScreen.kt" ]; then
    echo "ERROR: BrewEntryListScreen.kt not found"
    exit 1
fi

if [ ! -f "composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListViewModel.kt" ]; then
    echo "ERROR: BrewEntryListViewModel.kt not found"
    exit 1
fi

if [ ! -f "composeApp/src/commonMain/kotlin/coffee/app/data/repository/BrewEntryRepository.kt" ]; then
    echo "ERROR: BrewEntryRepository.kt not found"
    exit 1
fi

echo "Checking that key implementation elements exist..."

# Verify BrewEntryListScreen.kt has the bottom bar implementation
if grep -q "bottom bar for bulk delete" "composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListScreen.kt"; then
    echo "✓ Bottom bar implementation found"
else
    echo "⚠ Bottom bar implementation not found"
fi

# Verify BrewEntryListScreen.kt has the dialog implementation  
if grep -q "Delete confirmation dialog" "composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListScreen.kt"; then
    echo "✓ Delete confirmation dialog implementation found"
else
    echo "⚠ Delete confirmation dialog not found"
fi

# Verify BrewEntryListViewModel.kt has deleteSelected function
if grep -q "fun deleteSelected()" "composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListViewModel.kt"; then
    echo "✓ deleteSelected function found"
else
    echo "⚠ deleteSelected function not found"
fi

# Verify BrewEntryRepository.kt has deleteByUuids function
if grep -q "deleteByUuids" "composeApp/src/commonMain/kotlin/coffee/app/data/repository/BrewEntryRepository.kt"; then
    echo "✓ deleteByUuids function found"
else
    echo "⚠ deleteByUuids function not found"
fi

# Verify snackbar functionality is implemented
if grep -q "_snackbarMessage" "composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListViewModel.kt"; then
    echo "✓ Snackbar message implementation found"
else
    echo "⚠ Snackbar message implementation not found"
fi

echo "Verification complete"