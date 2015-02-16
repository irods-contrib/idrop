*** Settings ***
Documentation     To run SwingExplorer and iDrop
...
...               java -cp $HOME/Downloads/RobotFramework_SwingLibrary/v1.9.1/SWExplorer_v1.6/swexpl.jar:$HOME/Downloads/RobotFramework_SwingLibrary/v1.9.1/SWExplorer_v1.6/swag.jar:idrop-swing-2.0.1-RC1.jar:dependency/* org.swingexplorer.Launcher org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
...
...               To Define CLASSPATH for testing
...               export CLASSPATH=/Applications/iDrop\ Transfer\ Manager.app/Contents/java/app/idrop-swing-2.0.1-RC1.jar:dependency/*:/Users/jerry/Downloads/RobotFramework_SwingLibrary/v1.9.1/swinglibrary-1.9.1.jar
Resource          ../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop
    Start Application In Separate Thread    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Sleep    5s
    List Windows

Log Window titles and names
    [Tags]    smoke    iDrop    skipped
    List Windows

PassPhrase title
    [Tags]    smoke    iDrop
    Comment    Select Dialog    iDrop: Enter Pass Phrase
    Select Dialog    passPhraseDialog
    ${Title} =    Get Current Context
    Log    ${Title}

Gather Component Names
    [Tags]    smoke    iDrop
    Comment    Select Dialog    iDrop: Enter Pass Phrase
    List Components In Context
    List Components In Context    formatted

Cancel button exists
    [Tags]    smoke    iDrop
    Comment    Select Dialog    iDrop: Enter Pass Phrase
    Comment    Button Should Exist    Cancel
    Button Should Exist    btmCancel

Verify Cancel button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btmCancel
    Should Be Equal As Strings    ${ButtonText}    Cancel

Verify Cancel button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btmCancel    None

Cancel button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btmCancel

I Forgot button exists
    [Tags]    smoke    iDrop
    Button Should Exist    btnIForgot

Verify I Forgot button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnIForgot
    Should Be Equal As Strings    ${ButtonText}    I Forgot

Verify I Forgot button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnIForgot    None

I Forgot button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnIForgot

OK button exists
    [Tags]    smoke    iDrop
    Button Should Exist    btnOk

Verify OK button text
    [Tags]    smoke    iDrop    DICE-97
    ${ButtonText} =    Get Button Text    btnOk
    Should Be Equal As Strings    ${ButtonText}    OK

Verify OK button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnOk    None

OK button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnOk

PassPhrase Pass Phrase label exists
    [Tags]    smoke    iDrop    skipped
    Comment    Select Dialog    iDrop: Enter Pass Phrase
    Label Should Exist    lblPassPhrase

PassPhrase Pass Phrase editbox exists
    [Tags]    smoke    iDrop
    Comment    Select Dialog    iDrop: Enter Pass Phrase
    Component Should Exist    passPhrase

Confirm PassPhrase Pass Phrase editbox exists
    [Tags]    smoke    iDrop    skipped
    Comment    Component Should Exist    confirmPassPhrase
    Text Field Should Be Enabled    confirmPassPhrase
