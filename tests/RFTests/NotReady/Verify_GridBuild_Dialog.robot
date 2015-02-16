*** Settings ***
Suite Teardown    Close Dialog    gridInfoDialog
Resource          ../../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    skipped
    Start Application In Separate Thread    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Sleep    5s
    List Windows
    Comment    The Pass Phrase dialog window should already be open
    Select Dialog    passPhraseDialog
    Insert Into Text Field    passPhrase    ${Password}
    Push Button    btnOk
    Select Dialog    gridMemoryDialog
    Comment    The Grid window should already be open
    Select Table Cell    gridTable    0    0
    Push Button    btnLogIn
    Sleep    10s

Open Grid Select dialog
    [Tags]    smoke    iDrop
    Comment    The MainScreen dialog window should already be open
    Select Window    idropMainGui
    Push Button    btnGrids
    Select Dialog    gridMemoryDialog

Open Add New Grid dialog
    [Tags]    smoke    iDrop
    Comment    The iDrop: Grid Accounts dialog window should already be open
    Push Button    btnAddGrid
    Select Dialog    gridInfoDialog
    List Windows
    List Components In Context    formatted

Verify iDrop: Grid Accounts dialog title
    [Tags]    smoke    iDrop
    Select Dialog    gridInfoDialog
    ${TitleIs} =    Get Current Context
    Log    ${TitleIs}
    Should Be Equal As Strings    ${TitleIs}    Create Grid Account Information

Host editbox exists
    [Tags]    smoke    iDrop
    Component Should Exist    host

Confirm Host editbox is enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    host

Verify Host value
    [Tags]    smoke    iDrop
    ${CurValueIs} =    Get Text Field Value    host
    Should be Equal    ${CurValueIs}    ${EMPTY}

Verify Host editbox tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    host    None

Port editbox exists
    [Tags]    smoke    iDrop
    Component Should Exist    port

Confirm Port editbox is enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    port

Verify Port value
    [Tags]    smoke    iDrop
    ${CurValueIs} =    Get Text Field Value    port
    Should be Equal    ${CurValueIs}    1247

Verify Port editbox tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    port    None

Zone editbox exists
    [Tags]    smoke    iDrop
    Component Should Exist    zone

Confirm Zone editbox is enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    zone

Verify Zone value
    [Tags]    smoke    iDrop
    ${CurValueIs} =    Get Text Field Value    zone
    Should be Equal    ${CurValueIs}    ${EMPTY}

Verify Zone editbox tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    zone    None

User editbox exists
    [Tags]    smoke    iDrop
    Component Should Exist    user

Confirm User editbox is enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    user

Verify User value
    [Tags]    smoke    iDrop
    ${CurValueIs} =    Get Text Field Value    user
    Should be Equal    ${CurValueIs}    ${EMPTY}

Verify User editbox tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    user    None

Password editbox exists
    [Tags]    smoke    iDrop
    Component Should Exist    password

Confirm Password editbox is enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    password

Verify Password value
    [Tags]    smoke    iDrop
    ${CurValueIs} =    Get Text Field Value    password
    Should be Equal    ${CurValueIs}    ${EMPTY}

Verify Password editbox tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    password    None

Default Resource editbox exists
    [Tags]    smoke    iDrop
    Component Should Exist    defaultResource

Confirm Default Resource editbox is enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    defaultResource

Verify Default Resource value
    [Tags]    smoke    iDrop
    ${CurValueIs} =    Get Text Field Value    defaultResource
    Should be Equal    ${CurValueIs}    ${EMPTY}

Verify Default Resource editbox tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    defaultResource    None

Starting Collection editbox exists
    [Tags]    smoke    iDrop
    Component Should Exist    startingCollection

Confirm Starting Collection editbox is enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    startingCollection

Verify Starting Collection value
    [Tags]    smoke    iDrop
    ${CurValueIs} =    Get Text Field Value    startingCollection
    Should be Equal    ${CurValueIs}    ${EMPTY}

Verify Starting Collection editbox tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    startingCollection    None

Authorization Scheme dropdown list exits
    [Tags]    smoke    iDrop    skipped
    Component Should Exist    authorizationScheme

Authorization Scheme dropdown value
    [Tags]    smoke    iDrop    skipped
    ${CurValueIs} =    Get Text Field Value    authorizationScheme
    Should be Equal    ${CurValueIs}    ${EMPTY}

Comment textbox exists
    [Tags]    smoke    iDrop
    Component Should Exist    comment

Confirm Comment textbox is enabled
    [Tags]    smoke    iDrop
    Text Field Should Be Enabled    comment

Verify Comment value
    [Tags]    smoke    iDrop
    ${CurValueIs} =    Get Text Field Value    comment
    Should be Equal    ${CurValueIs}    ${EMPTY}

Verify Comment textbox tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    comment    None

Verify Cancel button exists
    [Tags]    smoke    iDrop
    Button Should Exist    btnCancel

Verify Cancel button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnCancel
    Should Be Equal As Strings    ${ButtonText}    ${EMPTY}

Verify Cancel button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnCancel    Cancel update

Verify Cancel button is enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnCancel

Verify OK button exists
    [Tags]    smoke    iDrop
    Button Should Exist    btnOK

Verify OK button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnOK
    Should Be Equal As Strings    ${ButtonText}    ${EMPTY}

Verify OK button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnOK    Save account data

Verify OK button is enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnOK
