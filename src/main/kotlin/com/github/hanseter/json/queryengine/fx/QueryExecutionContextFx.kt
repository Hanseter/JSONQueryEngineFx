package com.github.hanseter.json.queryengine

import java.util.concurrent.atomic.AtomicLong

class QueryExecutionContextFx<T : QuerieableData>(
	private val supplier: () -> T?,
	private val matchingItemFoundCallback: (T) -> Unit,
	private val finishedCallback: () -> Unit
) : QueryExecutionContext<T> {
	private val searchedElementCount = AtomicLong(0)
	private val matchesCount = AtomicLong(0)

	override fun getNextElement(): T? {
		val elem = supplier()
		if (elem != null) {
			searchedElementCount.incrementAndGet()
		}
		return elem
	}

	override fun addMatch(match: T) {
		matchesCount.incrementAndGet();
		matchingItemFoundCallback(match)
	}

	override fun executionFinished() = finishedCallback()

	fun getSearchedElementCount() = searchedElementCount.get();

	fun getMatchesCount() = matchesCount.get()
}