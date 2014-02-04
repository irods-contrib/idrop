package org.irods.jargon.idrop.desktop.systraygui.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.irods.jargon.idrop.desktop.systraygui.services.IdropConfigurationService;

/**
 * Access data about the configuration of Idrop. This serves as a view to the
 * loaded cache of properties that iDrop consults. The properties are originally
 * 'bootstrapped' at load time and resolved from various sources to come up with
 * the operative set. This bootstrapping is done by the
 * {@link IdropConfigurationService}.
 * <p/>
 * In normal operation, this config class is queried by iDrop to save database
 * accesses. When any configuration information is updated, this is through the
 * <code>IdropConfigurationService</code>, which will make necessary database
 * updates, and then update this cache.
 * 
 * @author Mike Conway - DICE (www.irods.org)
 */
public class IdropConfig {

	private final Properties idropProperties;

	/**
	 * Given a key, get the value in the cached properties (this is not going
	 * against the config database)
	 * 
	 * @param propKey
	 *            <code>String</code> with the key of the property
	 * @return <code>String</code> with the resulting value, or
	 *         <code>null</code> if not found
	 */
	public String getPropertyForKey(final String propKey) {
		return idropProperties.getProperty(propKey);
	}

	/**
	 * General method to set a property in the cached properties (this does not
	 * update the config database)
	 * 
	 * @param propKey
	 *            <code>String</code> with the name of the property, cannot be
	 *            null
	 * @param propValue
	 *            <code>String</code> with the value of the property, can be
	 *            null
	 */
	public void setProperty(final String propKey, final String propValue) {
		if (propKey == null) {
			throw new IllegalArgumentException("null propKey");
		}
		idropProperties.put(propKey, propValue);
	}

	public Properties getIdropProperties() {
		return idropProperties;
	}

	/**
	 * Initialize this wrapper around properties with the
	 * <code>Properties</code> that represent the idrop configuration.
	 * 
	 * @param properties
	 */
	public IdropConfig(final Properties properties) {
		if (properties == null) {
			throw new IllegalArgumentException("null properties");
		}
		idropProperties = properties;
	}

	/**
	 * Does iDrop need to display policy-aware features?
	 * 
	 * @return <code>boolean</code> that will be <code>true</code> if policy
	 *         features are displayed.
	 */
	public boolean isPolicyAware() {
		boolean policyAware = false;
		String policyAwareValue = idropProperties
				.getProperty(IdropConfigurationService.POLICY_AWARE_PROPERTY);

		if (policyAwareValue != null && policyAwareValue.equals("true")) {
			policyAware = true;
		}

		return policyAware;

	}

	/**
	 * Does iDrop need to display advanced options? Otherwise, a simpler client
	 * is presented
	 * 
	 * @return <code>boolean</code> that will be <code>true</code> if policy
	 *         features are displayed.
	 */
	public boolean isAdvancedView() {
		boolean advancedView = false;
		String propValue = idropProperties
				.getProperty(IdropPropertiesHelper.ADVANCED_VIEW_PROPERTY);

		if (propValue != null && propValue.equals("true")) {
			advancedView = true;
		}

		return advancedView;

	}

	public String getTransferDatabaseName() {
		String propValue = idropProperties
				.getProperty(IdropPropertiesHelper.TRANSFER_DATABASE_NAME);

		if (propValue != null) {
			return propValue;
		} else {
			return "transferDatabase";
		}
	}

	/**
	 * Should iDrop display a preset login limited to a user's home directory?
	 * 
	 * @return
	 */
	public boolean isLoginPreset() {
		boolean loginPreset = false;
		String loginPresetValue = idropProperties
				.getProperty(IdropPropertiesHelper.LOGIN_PRESET);

		if (loginPresetValue != null && loginPresetValue.equals("true")) {
			loginPreset = true;
		}

		return loginPreset;
	}

	/**
	 * Should successful transfers be logged to the internal database?
	 * 
	 * @return
	 */
	public boolean isLogSuccessfulTransfers() {
		boolean logSuccessful = false;
		String logSuccessfulTransfers = idropProperties
				.getProperty(IdropConfigurationService.TRANSFER_ENGINE_RECORD_SUCCESSFUL_FILES);

		if (logSuccessfulTransfers != null
				&& logSuccessfulTransfers.equals("true")) {
			logSuccessful = true;
		}

		return logSuccessful;
	}

	/**
	 * Should a checksum be created and verifed during get/put transfers?
	 * 
	 * @return
	 */
	public boolean isVerifyChecksum() {
		boolean verify = false;
		String verifyChecksumValue = idropProperties
				.getProperty(IdropConfigurationService.VERIFY_CHECKSUM_ON_TRANSFER);

		if (verifyChecksumValue != null && verifyChecksumValue.equals("true")) {
			verify = true;
		}

		return verify;
	}

	/**
	 * Should connections be re-routed on put/get based on file resource
	 * containing files
	 * 
	 * @return
	 */
	public boolean isAllowConnectionRerouting() {
		boolean bool = false;
		String val = idropProperties
				.getProperty(IdropConfigurationService.ALLOW_CONNECTION_REROUTING);

		if (val != null && val.equals("true")) {
			bool = true;
		}
		return bool;
	}

	/**
	 * Should transfer progress within a file be shown?
	 * 
	 * @return <code>boolean</code> that will be <code>true</code> if intra-file
	 *         call-backs are desired.
	 */
	public boolean isIntraFileStatusCallbacks() {
		return getBooleanForKey(IdropConfigurationService.INTRA_FILE_STATUS_CALLBACKS);
	}

	/**
	 * Time-out (in seconds) for the main iRODS connection. This can be set to 0
	 * or less to inactivate
	 * 
	 * @return
	 */
	public int getIrodsConnectionTimeout() {
		return getIntForKey(IdropConfigurationService.IRODS_CONNECTION_TIMEOUT);
	}

	/**
	 * Time-out (in seconds) for iRODS connections during parallel transfer.
	 * This can be set to 0 or less to inactivate
	 * 
	 * @return
	 */
	public int getIrodsParallelConnectionTimeout() {
		return getIntForKey(IdropConfigurationService.IRODS_PARALLEL_CONNECTION_TIMEOUT);
	}

	/**
	 * Maximum number of threads in parallel transfers. This is a trade-off in
	 * performance and through-put
	 * 
	 * @return
	 */
	public int getIrodsParallelTransferMaxThreads() {
		return getIntForKey(IdropConfigurationService.IRODS_PARALLEL_CONNECTION_MAX_THREADS);
	}

	/**
	 * Indicates whether a pool is used to maintain parallel transfer threads
	 * 
	 * @return
	 */
	public boolean isParallelUsePool() {
		return getBooleanForKey(IdropConfigurationService.IRODS_PARALLEL_USE_POOL);
	}

	/**
	 * Do I use parallel transfers at all?
	 * 
	 * @return
	 */
	public boolean isUseParallelTransfers() {
		return getBooleanForKey(IdropConfigurationService.IRODS_PARALLEL_USE_PARALLEL);
	}

	public boolean isUseNIOForParallelTransfers() {
		return getBooleanForKey(IdropConfigurationService.IRODS_PARALLEL_USE_NIO);
	}

	/**
	 * Get the internal buffer size used for the input stream between Jargon and
	 * iRODS. See https://code.renci.org/gf/project/jargon/wiki/?pagename=
	 * NormalIOArrangement return <code>int</code> with the buffer size for the
	 * input stream buffer. (0 = use defaults, -1 = do not wrap with buffered
	 * input stream) jargon.io.internal.input.stream.buffer.size
	 */
	public int getInternalInputStreamBufferSize() {
		return getIntForKey(IdropConfigurationService.IRODS_IO_INTERNAL_INPUT_STREAM_BUFFER_SIZE);
	}

	/**
	 * Get the internal buffer size used for the output stream between Jargon
	 * and iRODS. See https://code.renci.org/gf/project/jargon/wiki/?pagename=
	 * NormalIOArrangement return <code>int</code> with the buffer size for the
	 * output stream buffer. (0 = use defaults, -1 = do not wrap with buffered
	 * input stream) jargon.io.internal.output.stream.buffer.size
	 */
	public int getInternalOutputStreamBufferSize() {
		return getIntForKey(IdropConfigurationService.IRODS_IO_INTERNAL_OUTPUT_STREAM_BUFFER_SIZE);
	}

	/**
	 * Get the size of the internal buffer cache . See
	 * https://code.renci.org/gf/
	 * project/jargon/wiki/?pagename=NormalIOArrangement. Jargon has an internal
	 * buffer where the various <code>send()</code> methods in
	 * {@link IRODSConnection} write data to iRODS. In these methods, Jargon
	 * uses an internal cache buffer for the sends. This has been done
	 * historically, but the benefits of this cache have not yet been measured.
	 * Setting this as a parameter to turn off will assist in testing the use of
	 * the buffer, and the option of eliminating the buffer altogether. return
	 * <code>int</code> with the size of the internal cache (0 = do not utilize
	 * the cache buffer) jargon.io.internal.cache.buffer.size
	 */
	public int getInternalCacheBufferSize() {
		return getIntForKey(IdropConfigurationService.IRODS_IO_INTERNAL_CACHE_BUFFER_SIZE);
	}

	/**
	 * Get the buffer size used for the input stream between Jargon and iRODS
	 * passed to the <code>send()</code> method of {@link IRODSConnection}. This
	 * input stream would typically be from a local file that was being sent to
	 * iRODS, or other such source. The {@link IRODSCommands} object, using the
	 * <code>irodsFunction</code> method with the <code>InputStream</code>
	 * parameter, will wrap the given input stream in a
	 * <code>BufferedInputStream</code> based on the setting of this parameter.
	 * 
	 * See https://code.renci.org/gf/project/jargon/wiki/?pagename=
	 * NormalIOArrangement return <code>int</code> with the buffer size for the
	 * buffered stream that will wrap an <code>InputStream</code> to be sent to
	 * iRODS. (0 = use defaults, -1 = do not wrap with buffered input stream)
	 * jargon.io.send.input.stream.buffer.size
	 */
	public int getSendInputStreamBufferSize() {
		return getIntForKey(IdropConfigurationService.IRODS_IO_SEND_INPUT_STREAM_BUFFER_SIZE);
	}

	/**
	 * Get the size of the buffer used in read/write operations to copy data
	 * from an input stream to output stream in the {@link IRODSConnection}
	 * class <code>send()</code> methods.
	 * 
	 * @return <code>int</code> with the size of the read/write loop buffer
	 *         jargon.io.input.to.output.copy.byte.buffer.size
	 */
	public int getInputToOutputCopyBufferByteSize() {
		return getIntForKey(IdropConfigurationService.IRODS_IO_INPUT_TO_OUTPUT_COPY_BUFFER_SIZE);
	}

	/**
	 * Get the size of the buffer used in a <code>BufferedOutputStream</code>
	 * that wraps the output stream for the local file. This is used in
	 * processing get operations where the iRODS data is being saved to the
	 * local file system. (0 = use defaults, -1 = do not wrap with buffered
	 * output stream) jargon.io.local.output.stream.buffer.size
	 * 
	 * @return <code>int</code> with the buffer size
	 */
	public int getLocalFileOutputStreamBufferSize() {
		return getIntForKey(IdropConfigurationService.IRODS_IO_LOCAL_OUTPUT_STREAM_BUFFER_SIZE);
	}

	/**
	 * Get the size of the buffer used in a <code>BufferedInputStream</code>
	 * that wraps the intput stream for the local file. This is used in
	 * processing operations where the data is being read from the local file
	 * system. (0 = use defaults, -1 = do not wrap with buffered output stream)
	 * jargon.io.local.input.stream.buffer.size
	 * 
	 * @return <code>int</code> with the buffer size
	 */
	public int getLocalFileInputStreamBufferSize() {
		return getIntForKey(IdropConfigurationService.IRODS_IO_LOCAL_INPUT_STREAM_BUFFER_SIZE);
	}

	/**
	 * Get the size of the file segment for each successive call in normal put
	 * operations.
	 * 
	 * @return
	 */
	public int getPutBufferSize() {
		return getIntForKey(IdropConfigurationService.IRODS_IO_PUT_BUFFER_SIZE);
	}

	/**
	 * Get the size of the file segment for each successive call in normal get
	 * operations.
	 * 
	 * @return <code>int</code> with buffer size
	 */
	public int getGetBufferSize() {
		return getIntForKey(IdropConfigurationService.IRODS_IO_GET_BUFFER_SIZE);
	}

	/**
	 * Get the configured synch device name. If not set, this will return a
	 * <code>null</code>
	 * 
	 * @return
	 */
	public String getSynchDeviceName() {
		return idropProperties
				.getProperty(IdropConfigurationService.DEVICE_NAME);
	}

	/**
	 * Should the startup wizard be shown
	 * 
	 * @return
	 */
	public boolean isShowStartupWizard() {
		boolean showWizard = false;
		String showStartup = idropProperties
				.getProperty(IdropConfigurationService.SHOW_STARTUP);

		if (showStartup != null && showStartup.equals("true")) {
			showWizard = true;
		}

		return showWizard;

	}

	/**
	 * Should I have a rolling log in the user dir? Will return null of no
	 * logging desired, otherwise, will return a log level
	 * 
	 * @return
	 */
	public String getLogLevelForRollingLog() {
		String propValue = idropProperties
				.getProperty(IdropPropertiesHelper.ROLLING_LOG_LEVEL);
		return propValue;

	}

	public void setUpLogging() {
		String rollingLogLevel = getLogLevelForRollingLog();

		if (rollingLogLevel == null) {
			return;
		}

		// log level is specified, set up a rolling logger

		String userHomeDirectory = System.getProperty("user.home");
		StringBuilder sb = new StringBuilder();
		sb.append(userHomeDirectory);
		sb.append("/.idrop/idrop.log");

		org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger
				.getRootLogger();
		if (rollingLogLevel.equalsIgnoreCase("INFO")) {
			rootLogger.setLevel(Level.INFO);
		} else if (rollingLogLevel.equalsIgnoreCase("DEBUG")) {
			rootLogger.setLevel(Level.DEBUG);
		} else if (rollingLogLevel.equalsIgnoreCase("WARN")) {
			rootLogger.setLevel(Level.WARN);
		} else {
			rootLogger.setLevel(Level.ERROR);
		}

		PatternLayout layout = new PatternLayout(
				"%d %-4r [%t] %-5p %c %x - %m%n");

		try {
			RollingFileAppender rfa = new RollingFileAppender(layout,
					sb.toString());
			rfa.setMaximumFileSize(1000000);
			rootLogger.addAppender(rfa);
		} catch (IOException e) {
			// e.printStackTrace();
		}

	}

	public boolean isShowGuiAtStartup() {
		boolean propBoolean = false;
		String propString = idropProperties
				.getProperty(IdropConfigurationService.SHOW_GUI);

		if (propString != null && propString.equals("true")) {
			propBoolean = true;
		}

		return propBoolean;
	}

	private boolean getBooleanForKey(final String key) {
		boolean propBoolean = false;
		String propString = idropProperties.getProperty(key);

		if (propString != null && propString.equals("true")) {
			propBoolean = true;
		}
		return propBoolean;
	}

	private int getIntForKey(final String key) {
		int propInt = -1;
		String propString = idropProperties.getProperty(key);

		if (propString == null) {
			return propInt;
		}

		propInt = Integer.parseInt(propString.trim());

		return propInt;
	}

	public boolean isConnectionRestart() {
		boolean propBoolean = false;
		String propString = idropProperties
				.getProperty(IdropConfigurationService.IRODS_CONNECTION_RESTART);

		if (propString != null && propString.equals("true")) {
			propBoolean = true;
		}

		return propBoolean;
	}
}
