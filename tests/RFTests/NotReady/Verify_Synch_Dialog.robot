*** Settings ***
Suite Teardown    Close Dialog    synchronizationDialog
Resource          ../../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    RegSkip
    Open iDrop Main Window

Open Synch dialog window
    [Tags]    smoke    iDrop
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnSynch
    Select Dialog    synchronizationDialog
    List Components In Context    formatted

Add button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnAdd

Verify Add button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnAdd
    Should Be Equal As Strings    ${ButtonText}    Add

Verify Add button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnAdd    Add a new synch

Add button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnAdd

Delete button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnDelete

Verify Delete button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnDelete
    Should Be Equal As Strings    ${ButtonText}    Delete

Verify Delete button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnDelete    Delete the successful transfer from the queue

Delete button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnDelete

Launch button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnLaunch

Verify Launch button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnLaunch
    Should Be Equal As Strings    ${ButtonText}    Launch

Verify Launch button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnLaunch    Directly launch the synchronization

Launch button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnLaunch

Refresh button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnRefresh

Verify Refresh button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnRefresh
    Should Be Equal As Strings    ${ButtonText}    Refresh

Verify Refresh button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnRefresh    Reload this view

Refresh button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnRefresh

Synchronization List table exists
    [Tags]    smoke    iDrop
    Component Should Exist    synchronizationList

Synchronization List table should be visible
    [Tags]    smoke    iDrop
    Component Should Be Visible    synchronizationList

Name editbox should exist
    [Tags]    smoke    iDrop
    Component Should Exist    name

Name editbox should be visible
    [Tags]    smoke    iDrop
    Component Should Be Visible    name

Verify Name editbox tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    name    Name for synch

Local Directory button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnLocalDirectory

Verify Local Directory button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnLocalDirectory
    Should Be Equal As Strings    ${ButtonText}    Local Directory

Verify Local Directory button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnLocalDirectory    Find the local folder for the synch

Local Directory button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnLocalDirectory

iRODS Directory button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnIrodsDirectory

Verify iRODS Directory button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnIrodsDirectory
    Should Be Equal As Strings    ${ButtonText}    iRODS Directory

Verify iRODS Directory button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnIrodsDirectory    Find the iRODS folder for the synch

iRODS Directory button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnIrodsDirectory

Cancel button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnCancel

Verify Cancel button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnCancel
    Should Be Equal As Strings    ${ButtonText}    ${EMPTY}

Verify Cancel button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnCancel    Close the synch manager

Cancel button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnCancel

OK button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnOk

Verify OK button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnOk
    Should Be Equal As Strings    ${ButtonText}    ${EMPTY}

Verify OK button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnOk    Update the synchronization

OK button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnOk
