*** Settings ***
Suite Teardown    Close Dialog    Tools Dialog
Resource          ../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    RegSkip
    Open iDrop Main Window

Open Tools dialog window
    [Tags]    smoke    iDrop
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnTools
    Comment    Select Dialog    ToolsDialog
    Select Dialog    Tools Dialog
    List Components In Context    formatted

Data Tools dropdown should exist
    [Tags]    smoke    iDrop    DICE-88    skipped

Diff button should exist
    [Tags]    smoke    iDrop    DICE-88    skipped
    Button Should Exist    btnDiff

Verify Diff button text
    [Tags]    smoke    iDrop    DICE-88    skipped
    ${ButtonText} =    Get Button Text    btnDiff
    Should Be Equal As Strings    ${ButtonText}    Diff

Verify Diff button tooltip text
    [Tags]    smoke    iDrop    DICE-88    skipped
    Verify Tooltip Text    btnDiff    Diff a local and an iRODS file or collection

Diff button should be enabled
    [Tags]    smoke    iDrop    DICE-88    skipped
    Button Should Be Enabled    btnDiff

Exit button exits
    [Tags]    smoke    iDrop
    Button Should Exist    Exit

Verify Exit button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    Exit
    Should Be Equal As Strings    ${ButtonText}    Exit

Verify Exit button tooltip text
    [Tags]    smoke    iDrop    DICE-97
    Verify Tooltip Text    Exit    ${EMPTY}

Exit button is enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    Exit
