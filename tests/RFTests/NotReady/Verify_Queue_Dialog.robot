*** Settings ***
Suite Teardown    Close Dialog    transferAccountingManagerDialog
Resource          ../../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    RegSkip
    Open iDrop Main Window

Open Queue dialog window
    [Tags]    smoke    iDrop
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnQueue
    Select Dialog    transferAccountingManagerDialog
    List Components In Context    formatted

Verify Queue Title
    [Tags]    smoke    iDrop    skipped
    ${TitleIs} =    Get Selected Window Title
    Should Be Equal    ${TitleIs}    Manage iRODS Data Transfers

Purge All button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnPurgeAll

Verify Purge All button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnPurgeAll
    Should Be Equal As Strings    ${ButtonText}    Purge All

Verify Purge All button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnPurgeAll    Purge all transfers from the queue

Purge All button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnPurgeAll

Purge Successful button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnPurgeSuccessful

Verify Purge Successful button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnPurgeSuccessful
    Should Be Equal As Strings    ${ButtonText}    Purge Successful

Verify Purge Successful button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnPurgeSuccessful    Purge successful transfers from the queue

Purge Successful button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnPurgeSuccessful

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

Cancel button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnCancel

Verify Cancel button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnCancel
    Should Be Equal As Strings    ${ButtonText}    Cancel

Verify Cancel button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnCancel    Cancel a running transfer

Cancel button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnCancel

Restart button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnRestart

Verify Restart button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnRestart
    Should Be Equal As Strings    ${ButtonText}    Restart

Verify Restart button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnRestart    Restart the transfer after the last successful file

Restart button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnRestart

Resubmit button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnResubmit

Verify Resubmit button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnResubmit
    Should Be Equal As Strings    ${ButtonText}    Resubmit

Verify Resubmit button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnResubmit    Resubmit a transfer to start at the beginning

Resubmit button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnResubmit

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

Auto Refresh toggle button should not be selected
    [Tags]    smoke    iDrop
    Toggle Button Should Not Be Selected    btnAutoRefresh

Auto Refresh button should exist
    [Tags]    smoke    iDrop    skipped
    Comment    Button Should Exist    btnAutoRefresh
    Component Should Exist    btnAutoRefresh

Verify Auto Refresh button text
    [Tags]    smoke    iDrop    skipped
    ${ButtonText} =    Get Button Text    btnAutoRefresh
    Should Be Equal As Strings    ${ButtonText}    Auto Refresh

Verify Auto Refresh button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    btnAutoRefresh    Automatically refresh the queue display

Auto Refresh button should be enabled
    [Tags]    smoke    iDrop    skipped
    Comment    Button Should Be Enabled    btnAutoRefresh
    Component Should Be Enabled    btnAutoRefresh
