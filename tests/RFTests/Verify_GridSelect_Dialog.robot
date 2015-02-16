*** Settings ***
Resource          ../iDrop_resource.txt

*** Test Cases ***
Open Grid Select Dialog
    [Tags]    smoke    iDrop
    Comment    The Pass Phrase dialog window should already be open
    Select Dialog    passPhraseDialog
    Insert Into Text Field    passPhrase    ${PassPhrasePassword}
    Push Button    btnOk
    Select Dialog    gridMemoryDialog

GridTable should exist
    [Tags]    smoke    iDrop
    Component Should Exist    gridTable

Add Grid button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnAddGrid

Verify Add Grid button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnAddGrid
    Should Be Equal As Strings    ${ButtonText}    Add Grid

Verify Add Grid button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnAddGrid    Add a new remembered grid

Add Grid button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnAddGrid

Delete Grid button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnDeleteGrid

Verify Delete Grid button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnDeleteGrid
    Should Be Equal As Strings    ${ButtonText}    Delete Grid

Verify Delete Grid button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnDeleteGrid    Delete a grid that has been remembered

Delete Grid button should be disabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnDeleteGrid

Edit Grid button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnEditGrid

Verify Edit Grid button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnEditGrid
    Should Be Equal As Strings    ${ButtonText}    Edit Grid

Verify Edit Grid button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnEditGrid    Edit remembered grid information

Edit Grid button should be disabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnEditGrid

Cancel GridScreen button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnCancel

Verify Cancel GridScreen button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnCancel
    Should Be Equal As Strings    ${ButtonText}    Cancel

Verify Cancel button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnCancel    Cancel

Cancel GridScreen button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnCancel

Login button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnLogIn

Verify LogIn button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnLogIn
    Should Be Equal As Strings    ${ButtonText}    Login

Verify LogIn button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnLogIn    Login to the selected grid

LogIn button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnLogIn
