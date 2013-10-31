package org.irods.mydrop.service

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.RuleProcessingAO
import org.irods.jargon.core.utils.LocalFileUtils
import org.irods.jargon.core.utils.MiscIRODSUtils
import org.irods.jargon.ruleservice.composition.RuleCompositionService
import org.irods.jargon.ruleservice.composition.RuleCompositionServiceImpl


class RuleProcessingService {

	static transactional = false
	IRODSAccessObjectFactory irodsAccessObjectFactory

	def loadRuleFromIrodsFile(IRODSAccount irodsAccount, String rulePath) {

		RuleCompositionService ruleService = new RuleCompositionServiceImpl(irodsAccessObjectFactory, irodsAccount)
		log.info("attempting to retrieve rule for ${rulePath}")

		return ruleService.loadRuleFromIrods(rulePath)
	}

	def storeRule(IRODSAccount irodsAccount, String rulePath, String ruleBody, List<String> inputParameters, List<String> outputParameters) {

		log.info("storeRule")
		RuleCompositionService ruleService = new RuleCompositionServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return ruleService.storeRuleFromParts(rulePath, ruleBody, inputParameters, outputParameters)
	}

	def executeRule(IRODSAccount irodsAccount, String ruleBody, List<String> inputParameters, List<String> outputParameters) {

		log.info("executeRule")
		RuleCompositionService ruleService = new RuleCompositionServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return ruleService.executeRuleFromParts(ruleBody, inputParameters, outputParameters)
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

	def deleteOutputParam(IRODSAccount irodsAccount, String absPath, String parameter) {
		log.info("deleteOutputParam")
		RuleCompositionService ruleService = new RuleCompositionServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return ruleService.deleteOutputParameterFromRule(absPath, parameter)
	}


	def deleteInputParam(IRODSAccount irodsAccount, String absPath, String parameter) {
		log.info("deleteInputParam")
		RuleCompositionService ruleService = new RuleCompositionServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return ruleService.deleteInputParameterFromRule(absPath, parameter)
	}

	def addRuleInputParam(IRODSAccount irodsAccount, String absPath, String parameterName, String parameterValue) {
		log.info("addInputParam")
		RuleCompositionService ruleService = new RuleCompositionServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return ruleService.addInputParameterToRule(absPath, parameterName, parameterValue)
	}

	def addRuleOutputParam(IRODSAccount irodsAccount, String absPath, String parameterName) {
		log.info("addOutputParam")
		RuleCompositionService ruleService = new RuleCompositionServiceImpl(irodsAccessObjectFactory, irodsAccount)
		return ruleService.addOutputParameterToRule(absPath, parameterName)
	}

	def listDelayedRuleExecutions(IRODSAccount irodsAccount, int offset) {
		log.info("listDelayedRuleExecutions()")
		RuleProcessingAO ruleProcessingAO = irodsAccessObjectFactory.getRuleProcessingAO(irodsAccount)
		return ruleProcessingAO.listAllDelayedRuleExecutions(offset)
	}
}
