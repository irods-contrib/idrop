*** Settings ***
Resource          ../iDrop_resource.txt

*** Test Cases ***
Open Main Dialog window
    [Tags]    smoke    iDrop
    Comment    The Grid window should already be open
    ${TableRowCount} =    Get Table Row Count    gridTable
    Log    ${TableRowCount}
    ${HostRow} =    Find Row by Hostname    ${iRODSHost}    ${UserName}
    Log    ${HostRow}
    Select Table Cell    gridTable    ${HostRow}    Host
    Push Button    btnLogIn
    Comment    Sleep    10s
    Select Window    idropMainGui
    Comment    Open iDrop Main Window

Tree button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnNavigate

Verify Tree button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnNavigate
    Should Be Equal As Strings    ${ButtonText}    Navigate

Verify Tree button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnNavigate    Navigate

Tree button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnNavigate

Download button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnDownload

Verify Download button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnDownload
    Should Be Equal As Strings    ${ButtonText}    Download

Verify Download button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnDownload    None

Download button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnDownload

Upload button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnUpload

Verify Upload button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnUpload
    Should Be Equal As Strings    ${ButtonText}    Upload

Verify Upload button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnUpload    None

Upload button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnUpload

Refresh button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnRefresh

Verify Refresh button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnRefresh
    Should Be Equal As Strings    ${ButtonText}    Refresh

Verify Refresh button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnRefresh    None

Refresh button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnRefresh

New button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnNew

Verify New button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnNew
    Should Be Equal As Strings    ${ButtonText}    New

Verify New button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnNew    None

New button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnNew

Copy/Move button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnCopy

Verify Copy/Move button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnCopy
    Should Be Equal As Strings    ${ButtonText}    Copy

Verify Copy/Move button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnCopy    None

Copy/Move button should be disabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnCopy

Delete button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnDelete

Verify Delete button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnDelete
    Should Be Equal As Strings    ${ButtonText}    Delete

Verify Delete button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnDelete    None

Delete button should be disabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnDelete

Info button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnInfo

Verify Info button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnInfo
    Should Be Equal As Strings    ${ButtonText}    Info

Verify Info button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnInfo    Info about selected file or folder

Info button should be disabled
    [Tags]    smoke    iDrop
    Button Should Be Disabled    btnInfo

Tools button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnTools

Verify Tools button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnTools
    Should Be Equal As Strings    ${ButtonText}    Tools

Verify Tools button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnTools    Tools and utilities

Tools button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnTools

Settings button should exist
    [Tags]    smoke    iDrop
    Button Should Exist    btnSettings

Verify Settings button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    btnSettings
    Should Be Equal As Strings    ${ButtonText}    Settings

Verify Settings button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnSettings    None

Settings button should be enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    btnSettings

Queue button should exist
    [Tags]    smoke    iDrop    skipped
    Button Should Exist    btnQueue

Verify Queue button text
    [Tags]    smoke    iDrop    skipped
    ${ButtonText} =    Get Button Text    btnQueue
    Should Be Equal As Strings    ${ButtonText}    Queue

Verify Queue button tooltip text
    [Tags]    functional    iDrop    skipped
    Verify Tooltip Text    btnQueue    None

Queue button should be enabled
    [Tags]    smoke    iDrop    skipped
    Button Should Be Enabled    btnQueue

Grids button should exist
    [Tags]    smoke    iDrop    skipped
    Button Should Exist    btnGrids

Verify Grids button text
    [Tags]    smoke    iDrop    skipped
    ${ButtonText} =    Get Button Text    btnGrids
    Should Be Equal As Strings    ${ButtonText}    Grids

Verify Grids button tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    btnGrids    None

Grids button should be enabled
    [Tags]    smoke    iDrop    skipped
    Button Should Be Enabled    btnGrids

Synch button should exist
    [Tags]    smoke    iDrop    skipped
    Button Should Exist    btnSynch

Verify Synch button text
    [Tags]    smoke    iDrop    skipped
    ${ButtonText} =    Get Button Text    btnSynch
    Should Be Equal As Strings    ${ButtonText}    Synch

Verify Synch button tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    btnSynch    Manage synchronizations

Synch button should be enabled
    [Tags]    smoke    iDrop    skipped
    Button Should Be Enabled    btnSynch

Pause button should exist
    [Tags]    smoke    iDrop    skipped
    Button Should Exist    btnPause

Verify Pause button text
    [Tags]    smoke    iDrop    skipped
    ${ButtonText} =    Get Button Text    btnPause
    Should Be Equal As Strings    ${ButtonText}    Pause

Verify Pause button tooltip text
    [Tags]    functional    iDrop
    Verify Tooltip Text    btnPause    Pause processing
