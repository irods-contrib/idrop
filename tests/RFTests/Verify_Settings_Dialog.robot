*** Settings ***
Suite Teardown    Close Dialog    Settings
Resource          ../iDrop_resource.txt

*** Test Cases ***
Launch iDrop
    [Tags]    smoke    iDrop    RegSkip
    Open iDrop Main Window

Open Settings dialog window
    [Tags]    smoke    iDrop
    Comment    The Main iDrop window should already be open
    Select Window    idropMainGui
    Push Button    btnSettings
    Select Dialog    Settings
    List Components In Context    formatted

Verify Settings window title
    [Tags]    smoke    iDrop
    ${WindowTitles} =    List Windows
    ${ContextIs} =    Get Current Context
    Log    ${ContextIs}
    Comment    ${output} =    Get Selected Window Title
    Comment    Log    ${output}
    Should Be Equal As Strings    ${ContextIs}    Settings Dialog

Save button exits
    [Tags]    smoke    iDrop
    Button Should Exist    Save

Verify Save button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    Save
    Should Be Equal As Strings    ${ButtonText}    Save

Verify Save button tooltip text
    [Tags]    smoke    iDrop    DICE-97
    Verify Tooltip Text    Save    ${EMPTY}

Save button is enabled
    [Tags]    smoke    iDrop
    Button Should Be Enabled    Save

Select iDrop tab pane
    [Tags]    smoke    iDrop    skipped
    Select Tab As Context    iDrop
    List Components In Context    formatted

Cancel button exits
    [Tags]    smoke    iDrop
    Button Should Exist    Cancel

Verify Cancel button text
    [Tags]    smoke    iDrop
    ${ButtonText} =    Get Button Text    Cancel
    Should Be Equal As Strings    ${ButtonText}    Cancel

Verify Cancel button tooltip text
    [Tags]    smoke    iDrop    DICE-97
    Verify Tooltip Text    Cancel    ${EMPTY}

Cancel button is enabled
    [Tags]    smoke    iDrop    DICE-97
    Button Should Be Enabled    Cancel

Open iDrop user settings collapsible pane
    [Tags]    smoke    iDrop    DICE-97
    Select Context    iDrop user settings

Verify Manage data resources link
    [Tags]    smoke    iDrop    DICE-97
    Comment    Click Hyper Link    Manage data resources
    Click Hyper Link    btnPerformDiff

Verify Show iDROP GUI at startup checkbox enabled
    [Tags]    smoke    iDrop    DICE-97
    Check Box Should Be Enabled    chkShowGUI

Verify Show iDROP GUI at startup checkbox uncheck
    [Tags]    smoke    iDrop    DICE-97
    Check Box Should Be Unchecked    chkShowGUI

Verify Show file transfer progress checkbox enabled
    [Tags]    smoke    iDrop    DICE-97
    Check Box Should Be Enabled    chkShowTransferProgress

Verify Show file transfer progress checkbox uncheck
    [Tags]    smoke    iDrop    DICE-97
    Check Box Should Be Unchecked    chkShowTransferProgress

Open Data Transfer Settings collapsible pane
    [Tags]    smoke    iDrop    DICE-97
    Select Context    Data Transfer Settings

Open Test Connection collapsible pane
    [Tags]    smoke    iDrop    DICE-97
    Select Context    Test Connection

Open Pipeline Configuration collapsible pane
    [Tags]    smoke    iDrop    DICE-97
    Select Context    Pipeline Configuration

Verify Show Within checkbox is enabled
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Enabled    showWithinFileProgress

Verify Show Within Checkbox is checked
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Checked    showWithinFileProgress
    Comment    Click On Component    showWithinFileProgress
    Comment    Check Box Should Be Unchecked    showWithinFileProgress

Verify Show Within Checkbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    showWithinFileProgress    Show a progress bar that indicates the transfer progress within a file (bytes transferred versus total bytes)

Select Transfers tab pane
    [Tags]    smoke    iDrop    skipped
    Select Dialog    idropConfigurationPanel
    Select Tab As Context    Transfers
    List Components In Context    formatted

Verify Log Successful checkbox is enabled
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Enabled    logSuccessful

Verify Log Successful checkbox is checked
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Checked    logSuccessful
    Comment    Click On Component    logSuccessful
    Comment    Check Box Should Be Unchecked    logSuccessful

Verify Log Successful Checkbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    logSuccessful    Log each file in a transfer, can have performance impact

Verify Verify Checksum checkbox is enabled
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Enabled    verifyChecksum

Verify Verify Checksum checkbox is checked
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Checked    verifyChecksum
    Comment    Click On Component    verifyChecksum
    Comment    Check Box Should Be Unchecked    verifyChecksum

Verify Verify Checksum Checkbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    verifyChecksum    None

Verify Allow Connection Rerouting checkbox is enabled
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Enabled    alowConnectionRerouting

Verify Allow Connection Rerouting checkbox is checked
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Checked    alowConnectionRerouting
    Comment    Click On Component    alowConnectionRerouting
    Comment    Check Box Should Be Unchecked    alowConnectionRerouting

Verify Allow Connection Rerouting Checkbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    alowConnectionRerouting    Allows iDrop to connect directly to resources that hold particular data objects

Verify Connection Restart checkbox is enabled
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Enabled    connectionRestart

Verify Connection Restart checkbox is checked
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Checked    connectionRestart
    Comment    Click On Component    connectionRestart
    Comment    Check Box Should Be Unchecked    connectionRestart

Verify Connection Restart Checkbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    connectionRestart    Periodically restart the connection, equivalent to the -T option${SPACE}

Verify Max Transfers Before Failure spinner exists
    [Tags]    smoke    iDrop    skipped
    Spinner Should Exist    maxTransfersBeforeFailure

Verify Max Transfers Before Failure spinner value
    [Tags]    smoke    iDrop    skipped
    ${CurSpinnerValue} =    Get Spinner Value    maxTransfersBeforeFailure
    Should Be Equal    ${CurSpinnerValue}    ${5}

Verify Max Transfers Before Failure spinner tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    maxTransfersBeforeFailure    Number of transfer errors before quitting

Select Pipeline Configuration tab pane
    [Tags]    smoke    iDrop    skipped
    Select Dialog    idropConfigurationPanel
    Select Tab As Context    Pipeline Configuration
    List Components In Context    formatted

Verify Connection Timeout spinner exists
    [Tags]    smoke    iDrop    skipped
    Spinner Should Exist    connectionTimeout

Verify Connection Timeout spinner value
    [Tags]    smoke    iDrop    skipped
    ${CurSpinnerValue} =    Get Spinner Value    connectionTimeout
    Should Be Equal    ${CurSpinnerValue}    ${0}

Verify Connection Timeout spinner tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    connectionTimeout    Time out for the main iRODS agent connection

Verify Allow Parallel checkbox is enabled
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Enabled    allowParallel

Verify Allow Parallel checkbox is checked
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Checked    allowParallel
    Comment    Click On Component    allowParallel
    Comment    Check Box Should Be Unchecked    allowParallel

Verify Allow Parallel checkbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    allowParallel    Indicates whether parallel transfers are allowed

Verify Use NIO checkbox is enabled
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Enabled    useNio

Verify Use NIO checkbox is unchecked
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Unchecked    useNio
    Comment    Click On Component    useNio
    Comment    Check Box Should Be Unchecked    useNio

Verify Use NIO checkbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    useNio    Use Java NIO transfers for parallel transfer threads

Verify Use Exacutor Pool checkbox is enabled
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Enabled    useExecutorPool

Verify Use Exacutor Pool checkbox is unchecked
    [Tags]    smoke    iDrop    skipped
    Check Box Should Be Unchecked    useExecutorPool
    Comment    Click On Component    useExecutorPool
    Comment    Check Box Should Be Checked    useExecutorPool

Verify Use Exacutor Pool checkbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    useExecutorPool    None

Verify Internal Input Buffer textbox is enabled
    [Tags]    smoke    iDrop    skipped
    Text Field Should Be Enabled    internalInputBuffer

Verify Internal Input Buffer textbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    internalInputBuffer    Enter a buffer size for the internal buffer used between Jargon and iRODS.${SPACE*2}-1 means no buffer, 0 mean default buffer size, a positive value sets the buffer

Verify Internal Output Buffer textbox is enabled
    [Tags]    smoke    iDrop    skipped
    Text Field Should Be Enabled    internalOutputBuffer

Verify Internal Output Buffer textbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    internalOutputBuffer    Sets the output buffer size between jargon and iRODS.${SPACE*2}Note that this should be off by default, as Jargon does its own output buffering.${SPACE*2}There is a separate setting for \\n\n the internalCacheBuffer that dictates this buffer size.${SPACE*3}

Verify Local File Input Buffer Size textbox is enabled
    [Tags]    smoke    iDrop    skipped
    Text Field Should Be Enabled    localFileInputBufferSize

Verify Local File Input Buffer Size textbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    localFileInputBufferSize    Represents the buffer size for streaming from a local file.${SPACE*2}-1 means no buffering, 0 means use default buffer, a positive number sets a buffer size

Verify Local File Output Buffer Size textbox is enabled
    [Tags]    smoke    iDrop    skipped
    Text Field Should Be Enabled    localFileOutputBufferSize

Verify Local File Output Buffer Size textbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    localFileOutputBufferSize    Represents the buffer size for streaming to a local file. \\n${SPACE*2}-1 means no buffering, 0 means use default buffer, a positive number sets a buffer size

Verify Get Buffer Size textbox is enabled
    [Tags]    smoke    iDrop    skipped
    Text Field Should Be Enabled    getBufferSize

Verify Get Buffer Size textbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    getBufferSize    Represents the size of the binary data segment used in DataObjInp calls to iRODS for each call to get data

Verify Put Buffer Size textbox is enabled
    [Tags]    smoke    iDrop    skipped
    Text Field Should Be Enabled    putBufferSize

Verify Put Buffer Size textbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    putBufferSize    Represents the size of the binary data segment used in DataObjInp calls to iRODS for each call to put data

Verify Input To Output Copy Buffer Size textbox is enabled
    [Tags]    smoke    iDrop    skipped
    Text Field Should Be Enabled    inputToOutputCopyBufferSize

Verify Input To Output Copy Buffer Size textbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    inputToOutputCopyBufferSize    Sizes the buffer used internally for stream to stream copying

Verify Internal Cache Buffer Size textbox is enabled
    [Tags]    smoke    iDrop    skipped
    Text Field Should Be Enabled    internalCacheBufferSize

Verify Internal Cache Buffer Size textbox tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    internalCacheBufferSize    Represents the size of the binary data segment used in DataObjInp calls to iRODS for each call to put data

Verify Restore Default Settings button exists
    [Tags]    smoke    iDrop    skipped
    Button Should Exist    btnRestoreDefaultSettings

Verify Restore Default Settings button text
    [Tags]    smoke    iDrop    skipped
    ${ButtonText} =    Get Button Text    btnRestoreDefaultSettings
    Should Be Equal As Strings    ${ButtonText}    Restore default settings

Verify Restore Default Settings button tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    btnRestoreDefaultSettings    Sets pipeline configuration to suggested settings

Verify Restore Default Settings button is enabled
    [Tags]    smoke    iDrop    skipped
    Button Should Be Enabled    btnRestoreDefaultSettings

Verify Save Configuration button exists
    [Tags]    smoke    iDrop    skipped
    Button Should Exist    btnSaveConfiguration

Verify Save Configuration button text
    [Tags]    smoke    iDrop    skipped
    ${ButtonText} =    Get Button Text    btnSaveConfiguration
    Should Be Equal As Strings    ${ButtonText}    Save Configuration

Verify Save Configuration button tooltip text
    [Tags]    smoke    iDrop    skipped
    Verify Tooltip Text    btnSaveConfiguration    Update properties for the i/o pipeline

Verify Save Configuration button is enabled
    [Tags]    smoke    iDrop    skipped
    Button Should Be Enabled    btnSaveConfiguration
