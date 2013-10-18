package org.irods.mydrop.service

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory;
import org.irods.jargon.ruleservice.composition.RuleCompositionService
import org.irods.jargon.ruleservice.composition.RuleCompositionServiceImpl
import org.irods.jargon.core.utils.MiscIRODSUtils
import org.irods.jargon.core.utils.LocalFileUtils


class RuleProcessingService {
	
	static transactional = false
	IRODSAccessObjectFactory irodsAccessObjectFactory

    def loadRuleFromIrodsFile(IRODSAccount irodsAccount, String rulePath) {
		
		RuleCompositionService ruleService = new RuleCompositionServiceImpl(irodsAccessObjectFactory, irodsAccount)
		log.info("attempting to retrieve rule for ${rulePath}")
		
		return ruleService.loadRuleFromIrods(rulePath)
		
    }
	
	def isRule(String rulePath) {
		if (!rulePath) {
			return false
		}
		
		def fileName = MiscIRODSUtils.getLastPathComponentForGiveAbsolutePath(rulePath)
		def fileExtension = LocalFileUtils.getFileExtension(fileName)
		log.info("extension is:${fileExtension}")
			
		if (fileExtension && fileExtension == ".r") {
			return true
		} else {
			return false
		}		
		
	}

}
