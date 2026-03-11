package io.github.canjiemo.mydict

import com.intellij.psi.*
import com.intellij.psi.augment.PsiAugmentProvider
import com.intellij.psi.impl.light.LightFieldBuilder
import com.intellij.psi.impl.light.LightMethodBuilder

private const val MY_DICT_FQN = "io.github.canjiemo.tools.dict.MyDict"

class MyDictAugmentProvider : PsiAugmentProvider() {

    @Suppress("UNCHECKED_CAST")
    override fun <Psi : PsiElement> getAugments(
        element: PsiElement,
        type: Class<Psi>,
        nameHint: String?
    ): List<Psi> {
        if (element !is PsiClass || element.isAnnotationType || element.isInterface || element.isEnum) {
            return emptyList()
        }

        val result = mutableListOf<PsiElement>()

        for (field in element.fields) {
            val annotation = field.getAnnotation(MY_DICT_FQN) ?: continue
            val camelCase = resolveCamelCase(annotation)
            val descFieldName = DescNameResolver.resolve(field.name, camelCase)

            when {
                PsiField::class.java.isAssignableFrom(type) -> {
                    // 如果用户已手动定义该字段，跳过
                    if (element.findFieldByName(descFieldName, false) == null) {
                        result.add(buildDescField(element, field, descFieldName))
                    }
                }
                PsiMethod::class.java.isAssignableFrom(type) -> {
                    val accessorSuffix = toAccessorSuffix(descFieldName)
                    val getterName = "get$accessorSuffix"
                    val setterName = "set$accessorSuffix"

                    if (element.findMethodsByName(getterName, false).isEmpty()) {
                        result.add(buildGetter(element, field, getterName))
                    }
                    if (element.findMethodsByName(setterName, false).isEmpty()) {
                        result.add(buildSetter(element, field, descFieldName, setterName))
                    }
                }
            }
        }

        return result.filterIsInstance(type) as List<Psi>
    }

    private fun resolveCamelCase(annotation: PsiAnnotation): Boolean {
        val value = annotation.findAttributeValue("camelCase") ?: return true
        return (value as? PsiLiteralExpression)?.value as? Boolean ?: true
    }

    /** 将字段名转为访问器后缀（驼峰化），例如 user_status_desc → UserStatusDesc */
    private fun toAccessorSuffix(fieldName: String): String {
        return fieldName.split('_')
            .joinToString("") { it.replaceFirstChar { c -> c.uppercaseChar() } }
    }

    private fun buildDescField(
        containingClass: PsiClass,
        sourceField: PsiField,
        descFieldName: String
    ): PsiField {
        val factory = JavaPsiFacade.getElementFactory(containingClass.project)
        val stringType = factory.createTypeByFQClassName("java.lang.String", containingClass.resolveScope)
        return LightFieldBuilder(containingClass.manager, descFieldName, stringType).apply {
            setContainingClass(containingClass)
            setModifiers(PsiModifier.PRIVATE)
            navigationElement = sourceField
        }
    }

    private fun buildGetter(
        containingClass: PsiClass,
        sourceField: PsiField,
        getterName: String
    ): PsiMethod {
        val factory = JavaPsiFacade.getElementFactory(containingClass.project)
        val stringType = factory.createTypeByFQClassName("java.lang.String", containingClass.resolveScope)
        return LightMethodBuilder(containingClass.manager, getterName).apply {
            setContainingClass(containingClass)
            setMethodReturnType(stringType)
            addModifier(PsiModifier.PUBLIC)
            navigationElement = sourceField
        }
    }

    private fun buildSetter(
        containingClass: PsiClass,
        sourceField: PsiField,
        descFieldName: String,
        setterName: String
    ): PsiMethod {
        val factory = JavaPsiFacade.getElementFactory(containingClass.project)
        val stringType = factory.createTypeByFQClassName("java.lang.String", containingClass.resolveScope)
        return LightMethodBuilder(containingClass.manager, setterName).apply {
            setContainingClass(containingClass)
            setMethodReturnType(PsiTypes.voidType())
            addParameter(descFieldName, stringType)
            addModifier(PsiModifier.PUBLIC)
            navigationElement = sourceField
        }
    }
}
