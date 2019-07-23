package uk.ac.ebi.quickgo.annotation.model


import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.DEFAULT_GO_USAGE
import uk.ac.ebi.quickgo.annotation.model.AnnotationRequest.USAGE_RELATIONSHIP_PARAM
import uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern
import uk.ac.ebi.quickgo.rest.controller.request.ArrayPattern.Flag.CASE_INSENSITIVE
import javax.validation.Valid
import javax.validation.constraints.Pattern

/**
 * This class is a simple data class with builders having jvm overloads, written using kotlin.
 * Before that we decided to use loombok, but we think for data object/clear code kotlin is more useful
 * Look into git history AnnotationRequestBody.java with loombok annotation deleted (for reference)
 * Might be is future we decide to not use kotlin when java will introduce data objects (release 14 or 15)
 *
 * This class could be lot smaller and simpler but as all test expecting builders, i decided to not make any change at all
 * in other parts of application, that is how we can be sure about the seamless interoperability of kotlin with java
 *
 * For onwards we can simply use data object with jvmoverloads and copy constructor from kotlin to have clean code and easy to test
 */
data class AnnotationRequestBody @JvmOverloads constructor(@field:Valid var and: GoDescription? = null,
                                                           @field:Valid var not: GoDescription? = null) {

    companion object {
        @JvmStatic
        fun builder() = AnnotationRequestBody.Builder()

        @JvmStatic
        fun putDefaultValuesIfAbsent(requestBody: AnnotationRequestBody?) {
            if (requestBody == null)
                return
            if (requestBody.and == null) {
                requestBody.and = AnnotationRequestBody.GoDescription()
            }
            requestBody.and?.let { fillDefaultGoDescriptionIfNotPresent(it) }

            if (requestBody.not == null) {
                requestBody.not = AnnotationRequestBody.GoDescription()
            }
            requestBody.not?.let { fillDefaultGoDescriptionIfNotPresent(it) }
        }

        private fun fillDefaultGoDescriptionIfNotPresent(goDescription: AnnotationRequestBody.GoDescription) {
            val goUsage = goDescription.goUsage
            if (goUsage == null || goUsage.trim().isEmpty()) {
                goDescription.goUsage = DEFAULT_GO_USAGE
            }

            val goUsageRelationships = goDescription.goUsageRelationships
            if (goUsageRelationships == null || goUsageRelationships.isEmpty()) {
                goDescription.setGoUsageRelationships(AnnotationRequest.DEFAULT_GO_USAGE_RELATIONSHIPS)
            }
        }
    }

    data class Builder(
            var and: GoDescription? = null,
            var not: GoDescription? = null) {

        fun and(and: GoDescription) = apply { this.and = and }
        fun not(not: GoDescription) = apply { this.not = not }
        fun build() = AnnotationRequestBody(and, not)
    }


    class GoDescription {

        @JvmOverloads
        constructor (goTerms: Array<String> = emptyArray(),
                     goUsageRelationships: Array<String>? = null,
                     goUsage: String? = null) {
            this.goTerms = goTerms
            this.goUsageRelationships = goUsageRelationships
            this.goUsage = goUsage
        }

        companion object {
            @JvmStatic
            fun builder() = GoDescription.Builder()
        }

        @get:ArrayPattern(regexp = "^GO:[0-9]{7}$", flags = [CASE_INSENSITIVE], paramName = "goTerms")
        var goTerms: Array<String>

        @get:Pattern(regexp = "^descendants|exact$", flags = [Pattern.Flag.CASE_INSENSITIVE], message = "Invalid goUsage: \${validatedValue}")
        var goUsage: String?
            set(goUsage) {
                field = goUsage?.toLowerCase()
            }

        @get:ArrayPattern(regexp = "^is_a|part_of|occurs_in|regulates$", flags = [CASE_INSENSITIVE], paramName = USAGE_RELATIONSHIP_PARAM)
        var goUsageRelationships: Array<String>?
            private set

        fun setGoUsageRelationships(goUsageRelationships: String?) {
            this.goUsageRelationships = goUsageRelationships.orEmpty().split(",")
                    .filter { it.isNotBlank() }
                    .map { it.toLowerCase() }
                    .toTypedArray()
        }

        class Builder {
            private var goTerms: Array<String> = emptyArray()
            private var goUsage: String? = null
            private var goUsageRelationships: Array<String>? = null

            fun goUsageRelationships(vararg goUsageRelationships: String) =
                    apply { this.goUsageRelationships = goUsageRelationships.map { it.toLowerCase() }.toTypedArray(); }

            fun goUsage(goUsage: String?) = apply { this.goUsage = goUsage?.toLowerCase() }
            fun goTerms(goTerms: Array<String>) = apply { this.goTerms = goTerms }
            fun build() = GoDescription(goTerms, goUsageRelationships, goUsage)
        }
    }
}