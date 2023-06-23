package software.shattered.choir.dispatch.predicate

import software.shattered.choir.datastore.GenericDataStore

data class PredicateResult(val passed: Boolean = true, val data: GenericDataStore = GenericDataStore())