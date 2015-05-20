SELECT TRANSFER.TRANSFER.ID, TRANSFER.TRANSFER.TRANSFER_STATE,
  TRANSFER.TRANSFER.LAST_TRANSFER_STATUS, TRANSFER.TRANSFER.TRANSFER_TYPE,
  TRANSFER.TRANSFER.LOCAL_ABSOLUTE_PATH, TRANSFER.TRANSFER.IRODS_ABSOLUTE_PATH,
  TRANSFER.TRANSFER_ATTEMPT.SEQUENCE_NUMBER, TRANSFER.TRANSFER_ATTEMPT.TRANSFER_ATTEMPT_START,
  TRANSFER.TRANSFER_ATTEMPT.TRANSFER_ATTEMPT_END, TRANSFER.TRANSFER_ATTEMPT.TRANSFER_ATTEMPT_STATUS,
  TRANSFER.TRANSFER_ATTEMPT.ERROR_MESSAGE, TRANSFER.TRANSFER_ATTEMPT.GLOBAL_EXCEPTION,
  TRANSFER.TRANSFER_ATTEMPT.GLOBAL_EXCEPTION_STACK_TRACE, TRANSFER.TRANSFER_ATTEMPT.LAST_SUCCESSFUL_PATH,
  TRANSFER.TRANSFER_ATTEMPT.TOTAL_FILES_COUNT, TRANSFER.TRANSFER_ATTEMPT.TOTAL_FILES_TRANSFERRED_SO_FAR,
  TRANSFER.TRANSFER_ITEM.TARGET_FILE_ABSOLUTE_PATH, TRANSFER.TRANSFER_ITEM.TRANSFER_TYPE,
  TRANSFER.TRANSFER_ITEM.IS_FILE, TRANSFER.TRANSFER_ITEM.IS_SKIPPED,
  TRANSFER.TRANSFER_ITEM.IS_ERROR, TRANSFER.TRANSFER_ITEM.LENGTH_IN_BYTES,
  TRANSFER.TRANSFER_ITEM.ERROR_MESSAGE, TRANSFER.TRANSFER_ITEM.ERROR_STACK_TRACE
  FROM
       TRANSFER.TRANSFER_ITEM RIGHT OUTER JOIN TRANSFER.TRANSFER_ATTEMPT RIGHT OUTER JOIN TRANSFER.TRANSFER ON TRANSFER.TRANSFER_ATTEMPT.TRANSFER_ID = TRANSFER.TRANSFER.ID ON TRANSFER.TRANSFER_ITEM.TRANSFER_ATTEMPT_ID = TRANSFER.TRANSFER_ATTEMPT.ID
