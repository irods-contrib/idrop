*** Settings ***
Resource          ../../iDrop_resource.txt    #Suite Teardown | Close Window | idropMainGui

*** Test Cases ***
Start up iDrop and Log in
    [Tags]    smoke    iDrop
    Comment    Open iDrop Main Window
    Comment    List Components In Context
    Start Application In Separate Thread    org.irods.jargon.idrop.desktop.systraygui.IDROPDesktop
    Sleep    15s
    Select Dialog    dialog0
    ${Context} =    Get Current Context
    Log    ${Context}
    List Components In Context
    Sleep    10s
    ${Context} =    Get Current Context
    Log    ${Context}
    List Components In Context
    Comment    Check Check Box    Login As Guest
    Comment    Sleep    15s
    [Teardown]    Close All Dialogs
