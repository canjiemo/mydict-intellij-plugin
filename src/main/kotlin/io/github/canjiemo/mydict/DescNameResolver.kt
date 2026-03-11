package io.github.canjiemo.mydict

object DescNameResolver {

    fun resolve(fieldName: String, camelCase: Boolean): String {
        if (fieldName.contains('_')) {
            return if (fieldName == fieldName.uppercase()) {
                "${fieldName}_DESC"
            } else {
                "${fieldName}_desc"
            }
        }
        if (hasMixedCase(fieldName)) {
            return "${fieldName}Desc"
        }
        return if (camelCase) "${fieldName}Desc" else "${fieldName}_desc"
    }

    private fun hasMixedCase(str: String): Boolean {
        var hasLower = false
        var hasUpper = false
        for (c in str) {
            if (c.isLowerCase()) hasLower = true
            if (c.isUpperCase()) hasUpper = true
            if (hasLower && hasUpper) return true
        }
        return false
    }
}
