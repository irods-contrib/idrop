*** Settings ***
Suite Teardown    Close Dialog    uploadDialog
Resource          ../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    RegSkip
    Open iDrop Main Window

Open Upload dialog window
    [Tags]    smoke    iDrop
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnUpload
    Select Dialog    uploadDialog
    List Components In Context    formatted

Browse for a directory button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnBrowseForDirectory

Verify Browse for a directory button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnBrowseForDirectory
    Should Be Equal As Strings    ${ButtonText}    Browse

Verify Browse for a directory button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnBrowseForDirectory    Browse for a directory

Browse for a directory button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnBrowseForDirectory

Use iRODS home directory button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnUseIrodsHomeDirectory

Verify Use iRODS home directory button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnUseIrodsHomeDirectory
    Should Be Equal As Strings    ${ButtonText}    Use Home Directory

Verify Use iRODS home directory button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnUseIrodsHomeDirectory    None

Use iRODS home directory button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnUseIrodsHomeDirectory

Upload File List table should exist
    [Tags]    smoke    iDrop
    Component Should Exist    uploadFilesList

File List table should be visible
    [Tags]    smoke    iDrop
    Component Should Not Be Visible    uploadFilesList

Add File button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnAddFile

Verify Add File button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnAddFile
    Should Be Equal As Strings    ${ButtonText}    Add File

Verify Add File button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnAddFile    Add a file to the uploads

Add File button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnAddFile

Remove File button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnRemoveFile

Verify Remove File button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnRemoveFile
    Should Be Equal As Strings    ${ButtonText}    Remove File

Verify Remove File button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnRemoveFile    Remove a file from the downloads

Remove File button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnRemoveFile

Cancel button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnCancel

Verify Cancel button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnCancel
    Should Be Equal As Strings    ${ButtonText}    Cancel

Verify Cancel button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnCancel    Cancel the operation

Cancel button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnCancel

Upload button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnUpload

Verify Upload button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnUpload
    Should Be Equal As Strings    ${ButtonText}    Upload

Verify Upload button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnUpload    Start the upload

Upload button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnUpload
