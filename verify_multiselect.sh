#!/bin/bash

echo "=== Multi-Select Feature Verification ==="

# Check that our changes are present in the files
echo "Checking ViewModel changes..."
if grep -q "_selectedIds" composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListViewModel.kt; then
    echo "✓ ViewModel: Selection state found"
else
    echo "✗ ViewModel: Selection state NOT found"
    exit 1
fi

if grep -q "toggleSelection" composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListViewModel.kt; then
    echo "✓ ViewModel: toggleSelection function found"
else
    echo "✗ ViewModel: toggleSelection function NOT found"
    exit 1
fi

echo "Checking Screen changes..."
if grep -q "combinedClickable" composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListScreen.kt; then
    echo "✓ Screen: combinedClickable found"
else
    echo "✗ Screen: combinedClickable NOT found"
    exit 1
fi

if grep -q "showClose.*true" composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListScreen.kt; then
    echo "✓ Screen: showClose integration found"
else
    echo "✗ Screen: showClose integration NOT found"
    exit 1
fi

if grep -q "BackHandler" composeApp/src/commonMain/kotlin/coffee/app/list/BrewEntryListScreen.kt; then
    echo "✓ Screen: BackHandler found"
else
    echo "✗ Screen: BackHandler NOT found"
    exit 1
fi

echo "Checking TopBar changes..."
if grep -q "showClose.*Boolean" composeApp/src/commonMain/kotlin/coffee/app/core/TopBar.kt; then
    echo "✓ TopBar: showClose parameter found"
else
    echo "✗ TopBar: showClose parameter NOT found"
    exit 1
fi

echo ""
echo "=== Compilation Check ==="
./gradlew compileDebugKotlinAndroid --console=plain > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "✓ Compilation successful"
else
    echo "✗ Compilation failed"
    exit 1
fi

echo ""
echo "=== All verifications passed! ==="
echo "Multi-select mode implementation is ready."
