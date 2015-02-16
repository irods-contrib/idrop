*** Settings ***
Suite Teardown    Close Dialog    iDrop Message
Resource          ../../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    RegSkip
    Open iDrop Main Window

Open New dialog window nothing selected
    [Tags]    smoke    iDrop
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnNew
    Select Dialog    iDrop Message
    List Components In Context    formatted

Message Textbox should exist
    [Tags]    smoke    iDrop
    Component Should Exist    OptionPane.label

Message Textbox should be visible
    [Tags]    smoke    iDrop
    Component Should Be Visible    OptionPane.label

Message Textbox should enabled
    [Tags]    smoke    iDrop    skipped
    Text Field Should Be Enabled    OptionPane.label

OK button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    OptionPane.button

Verify OK button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    OptionPane.button
    Should Be Equal As Strings    ${ButtonText}    OK

Verify OK button tooltip text
    [Tags]    smoke    iDrop
    Verify Tooltip Text    OptionPane.button    None

Diff OK should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    OptionPane.button
