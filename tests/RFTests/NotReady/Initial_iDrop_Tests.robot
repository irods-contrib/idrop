*** Settings ***
Documentation     To run SwingExplorer and iDrop
...
...               java -cp $HOME/Downloads/RobotFramework_SwingLibrary/v1.9.1/SWExplorer_v1.6/swexpl.jar:$HOME/Downloads/RobotFramework_SwingLibrary/v1.9.1/SWExplorer_v1.6/swag.jar:idrop-swing-2.0.1-RC1.jar:dependency/* org.swingexplorer.Launcher org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
...
...               To Define CLASSPATH for testing
...               export CLASSPATH=/Applications/iDrop\ Transfer\ Manager.app/Contents/java/app/idrop-swing-2.0.1-RC1.jar:dependency/*:/Users/jerry/Downloads/RobotFramework_SwingLibrary/v1.9.1/swinglibrary-1.9.1.jar
Resource          ../../iDrop_resource.txt

*** Test Cases ***
PlaceHolder iDrop Test Suite
    [Tags]    functional    iDrop    skipped
    Comment    Define variables to be used

Running Yet orig
    [Tags]    functional    iDrop    skipped
    Comment    Try to start iDrop application
    Comment    Start iDrop Application
    Comment    Set Environment Variable    CLASSPATH    %{CLASSPATH}:/Applications/iDrop\ Transfer\ Manager.app/Contents/java/app/idrop-swing-2.0.1-RC1.jar:dependency/*
    ${output} =    Run    set \| grep CLASSPATH
    Log    ${output}
    Comment    Launch Application    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Start Application In Separate Thread    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Sleep    5s
    Comment    Select Dialog    class=org.irods.jargon.idrop.desktop.systraygui.LoginDialog
    Comment    Select Dialog    ${EMPTY}
    Comment    Select Dialog    name=dialog0
    Comment    Close Dialog    dialog0
    List Windows
    Comment    Select Window    frame0
    Select Dialog    iDrop: Enter Pass Phrase
    List Components In Context
    List Components In Context    formatted
    Insert Into Text Field    passPhrase    ${Password}
    Push Button    OK
    Comment    Close All Dialogs
    Comment    Select Window    regexp=^Login.*
    Comment    Select Dialog    LoginDialog()
    Comment    Insert Into Text Field    JTextField()    ${UserName}
    Comment    Insert Into Text Field    JPasswordField()    ${Password}
    Comment    Push Button    JButton()
    Comment    Simple check to see if iDrop launches okay
    Comment    Insert Into Text Field    LoginDialog[0]/JRootPane[1]/JLayeredPane[0]/JPanel[0]/JPanel[9]/JTextField    ${UserName}
    Comment    Insert Into Text Field    LoginDialog[0]/JRootPane[1]/JLayeredPane[0]/JPanel[0]/JPanel[11]/JPasswordField    ${Password}
    Comment    Push Button    LoginDialog[0]/JRootPane[1]/JLayeredPane[0]/JPanel[1]/JPanel[1]/JButton

Running Yet
    [Tags]    functional    iDrop
    Comment    Try to start iDrop application
    Comment    Start iDrop Application
    Comment    Set Environment Variable    CLASSPATH    %{CLASSPATH}:/Applications/iDrop\ Transfer\ Manager.app/Contents/java/app/idrop-swing-2.0.1-RC1.jar:dependency/*
    ${output} =    Run    set \| grep CLASSPATH
    Log    ${output}
    Comment    Launch Application    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Start Application In Separate Thread    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Sleep    5s
    List Windows
    Select Dialog    iDrop: Enter Pass Phrase
    List Components In Context
    List Components In Context    formatted
    Button Should Exist    Cancel
    Button Should Exist    I Forgot
    Button Should Exist    OK
    Component Should Exist    passPhrase
    Insert Into Text Field    passPhrase    ${Password}
    Push Button    OK
    Select Dialog    iDrop: Grid Accounts
    List Components In Context
    List Components In Context    formatted
    Button Should Exist    btnAddGrid
    Button Should Exist    btnDeleteGrid
    Button Should Exist    btnEditGrid
    Button Should Exist    btnCancel
    Button Should Exist    btnLogIn

is it running yet
    [Tags]    functional    iDrop    skipped
    Comment    Try to start iDrop application
    Start iDrop Application
    Comment    Simple check to see if iDrop launches okay
    Insert Into Text Field    LoginDialog[0]/JRootPane[1]/JLayeredPane[0]/JPanel[0]/JPanel[9]/JTextField    ${UserName}
    Insert Into Text Field    LoginDialog[0]/JRootPane[1]/JLayeredPane[0]/JPanel[0]/JPanel[11]/JPasswordField    ${Password}
    Push Button    LoginDialog[0]/JRootPane[1]/JLayeredPane[0]/JPanel[1]/JPanel[1]/JButton

*** Keywords ***
Start iDrop Application
    Comment    Set Environment Variable    CLASSPATH    "$CLASSPATH:/Applications/iDrop Transfer Manager.app/Contents/java/app/idrop-swing-2.0.1-RC1.jar:dependency/*"
    Comment    Set Environment Variable    CLASSPATH    %{CLASSPATH}:/Applications/iDrop\ Transfer\ Manager.app/Contents/java/app/idrop-swing-2.0.1-RC1.jar:dependency/*
    ${output} =    Run    set \| grep CLASSPATH
    Log    ${output}
    Launch Application    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
