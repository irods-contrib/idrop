package org.irods.mydrop.controller

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.exception.JargonException
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.rule.IRODSRuleExecResult
import org.irods.jargon.ruleservice.composition.Rule
import org.irods.jargon.ruleservice.formatting.HtmlLogTableFormatter
import org.irods.mydrop.service.RuleProcessingService


class RuleController {

	IRODSAccessObjectFactory irodsAccessObjectFactory
	IRODSAccount irodsAccount
	RuleProcessingService ruleProcessingService
	def grailsApplication

	/**
	 * Interceptor grabs IRODSAccount from the SecurityContextHolder
	 */
	def beforeInterceptor = [action:this.&auth]

	def auth() {
		if(!session["SPRING_SECURITY_CONTEXT"]) {
			redirect(controller:"login", action:"login")
			return false
		}
		irodsAccount = session["SPRING_SECURITY_CONTEXT"]
	}

	def afterInterceptor = {
		log.debug("closing the session")
		irodsAccessObjectFactory.closeSession()
	}

	def updateRule() {
		log.info("update rule")
		log.info("params:${params}")

		def absPath = params['ruleAbsPath']
		if (absPath == null) {
			log.error "no ruleAbsPath in request "
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		def ruleBody = params['ruleBody']
		if (!ruleBody) {
			log.error "no ruleBody in request "
			def message = message(code:"error.no.rule.body")
			response.sendError(500,message)
		}

		List<String> inputParams = new ArrayList<String>()
		List<String> inputParamValues = new ArrayList<String>()
		List<String> outputParams = new ArrayList<String>()

		def parmKey = params['inputParamName']
		def parmValue = params['inputParamValue']

		if (parmKey) {

			if (parmKey instanceof Object[]) {
				inputParams = parmKey
			} else {
				inputParams.add(parmKey)
			}

			if (!parmValue) {
				log.error "no param values for param keys in request "
				def message = message(code:"error.invalid.request")
				response.sendError(500,message)
			}

			if (parmValue instanceof Object[]) {
				inputParamValues = parmValue
			} else {
				inputParamValues.add(parmValue)
			}
		}

		parmKey = params['outputParamName']

		if (parmKey) {

			if (parmKey instanceof Object[]) {
				outputParams = parmKey
			} else {
				outputParams.add(parmKey)
			}
		}


		List<String> concatParams = new ArrayList<String>()
		for (int i = 0; i < inputParams.size(); i++) {
			concatParams.add(inputParams[i] + "=" + inputParamValues[i])
		}

		Rule rule = ruleProcessingService.storeRule(irodsAccount, absPath, ruleBody, concatParams, outputParams)
		log.info("rule stored:${rule}")
		render(view:"_ruleDetails", model:[absPath:absPath, rule:rule])
	}

	def deleteRuleInputParameter() {
		log.info("deleteRuleInputParameter()")
		log.info("params:${params}")

		def absPath = params['ruleAbsPath']
		if (!absPath) {
			log.error "no ruleAbsPath in request "
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}


		def parmKey = params['inputParamName']
		if (!parmKey) {
			log.error "no parmkey in request "
			def message = message(code:"error.invalid.request")
			response.sendError(500,message)
		}

		Rule rule = ruleProcessingService.deleteInputParam(irodsAccount, absPath, parmKey)
		log.info("rule stored:${rule}")
		render(view:"_ruleDetails", model:[absPath:absPath, rule:rule])
	}

	def reloadRule() {

		log.info("reloadRule()")

		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request "
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		try {
			def rule = ruleProcessingService.loadRuleFromIrodsFile(irodsAccount, absPath)
			log.info("found rule:${rule}")
			render(view:"_ruleDetails", model:[absPath:absPath, rule:rule])
		} catch (JargonException je) {
			log.error("unable to load rule", je)
			def message = message(code:"error.unable.to.load.rule")
			response.sendError(500,message)
		}
	}



	def index() {

		log.info("index()")

		def absPath = params['absPath']
		if (absPath == null) {
			log.error "no absPath in request "
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		try {
			def rule = ruleProcessingService.loadRuleFromIrodsFile(irodsAccount, absPath)
			log.info("found rule:${rule}")
			render(view:"index", model:[absPath:absPath, rule:rule])
		} catch (JargonException je) {
			log.error("unable to load rule", je)
			def message = message(code:"error.unable.to.load.rule")
			response.sendError(500,message)
		}
	}


	def runRule() {
		log.info("runRule()")
		log.info("params:${params}")

		def absPath = params['ruleAbsPath']
		if (absPath == null) {
			log.error "no ruleAbsPath in request "
			def message = message(code:"error.no.path.provided")
			response.sendError(500,message)
		}

		def ruleBody = params['ruleBody']
		if (!ruleBody) {
			log.error "no ruleBody in request "
			def message = message(code:"error.no.rule.body")
			response.sendError(500,message)
		}

		List<String> inputParams = new ArrayList<String>()
		List<String> inputParamValues = new ArrayList<String>()
		List<String> outputParams = new ArrayList<String>()

		def parmKey = params['inputParamName']
		def parmValue = params['inputParamValue']

		if (parmKey) {

			if (parmKey instanceof Object[]) {
				inputParams = parmKey
			} else {
				inputParams.add(parmKey)
			}

			if (!parmValue) {
				log.error "no param values for param keys in request "
				def message = message(code:"error.invalid.request")
				response.sendError(500,message)
			}

			if (parmValue instanceof Object[]) {
				inputParamValues = parmValue
			} else {
				inputParamValues.add(parmValue)
			}
		}

		parmKey = params['outputParamName']

		if (parmKey) {

			if (parmKey instanceof Object[]) {
				outputParams = parmKey
			} else {
				outputParams.add(parmKey)
			}
		}

		List<String> concatParams = new ArrayList<String>()
		for (int i = 0; i < inputParams.size(); i++) {
			concatParams.add(inputParams[i] + "=" +  inputParamValues[i])
		}

		try {
			IRODSRuleExecResult ruleResult = ruleProcessingService.executeRule(irodsAccount, ruleBody, concatParams, outputParams)
			log.info("rule result:${ruleResult}")
			def execOut = HtmlLogTableFormatter.formatAsBootstrap2Table(ruleResult.ruleExecOut, "Std Out")
			def errorOut = HtmlLogTableFormatter.formatAsBootstrap2Table(ruleResult.ruleExecErr, "Error Out")
			render(view:"ruleResult", model:[ruleResult:ruleResult, execOut:execOut, errorOut:errorOut])
		} catch (JargonException je) {
			def message = HtmlLogTableFormatter.formatAsBootstrap2Table(je.message, "Error")
			def stackTrace = HtmlLogTableFormatter.formatStackTraceAsBootstrap2Table(je)
			render(view:"ruleErrorResult", model:[message:message,stackTrace:stackTrace])
		}
	}
}
