*** Settings ***
Suite Teardown    Close Dialog    downloadDialog
Resource          ../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    RegSkip
    Open iDrop Main Window

Open Download dialog window
    [Tags]    smoke    iDrop
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnDownload
    Select Dialog    downloadDialog
    List Components In Context    formatted

File List table should exist
    [Tags]    smoke    iDrop
    Component Should Exist    fileList

File List table should be visible
    [Tags]    smoke    iDrop
    Component Should Not Be Visible    fileList

Add File button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnAddFile

Verify Add File button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnAddFile
    Should Be Equal As Strings    ${ButtonText}    Add file

Verify Add File button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnAddFile    Add a file to the downloads

Add File button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnAddFile

Delete File button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnDeleteFile

Verify Delete File button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnDeleteFile
    Should Be Equal As Strings    ${ButtonText}    Delete

Verify Delete File button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnDeleteFile    Delete a file from the downloads

Delete File button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnDeleteFile

Browse Local Folders button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnBrowseLocal

Verify Browse Local Folders button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnBrowseLocal
    Should Be Equal As Strings    ${ButtonText}    Browse

Verify Browse Local Folders button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnBrowseLocal    None

Browse Local Folders button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnBrowseLocal

Use Local Home Directory button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnUseLocalHomeDir

Verify Use Local Home Directory button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnUseLocalHomeDir
    Should Be Equal As Strings    ${ButtonText}    Use Local Home

Verify Use local home directory button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnUseLocalHomeDir    None

Use Local Home Directory button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnUseLocalHomeDir

Cancel button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnCancel

Verify Cancel button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnCancel
    Should Be Equal As Strings    ${ButtonText}    Cancel

Verify Cancel button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnCancel    Cancel the operation

Cancel button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnCancel

Download button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnDownload

Verify Download button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnDownload
    Should Be Equal As Strings    ${ButtonText}    Download

Verify Download button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnDownload    Start the download

Download button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnDownload
