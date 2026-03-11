package io.github.canjiemo.mydict

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DescNameResolverTest {

    @Test
    fun `snake_case field gets snake_case desc`() =
        assertEquals("user_status_desc", DescNameResolver.resolve("user_status", camelCase = true))

    @Test
    fun `UPPER_SNAKE field gets UPPER_SNAKE_DESC`() =
        assertEquals("USER_STATUS_DESC", DescNameResolver.resolve("USER_STATUS", camelCase = true))

    @Test
    fun `camelCase field gets camelCase desc`() =
        assertEquals("userNameDesc", DescNameResolver.resolve("userName", camelCase = true))

    @Test
    fun `all-lowercase with camelCase=true gets camelCase desc`() =
        assertEquals("statusDesc", DescNameResolver.resolve("status", camelCase = true))

    @Test
    fun `all-lowercase with camelCase=false gets snake desc`() =
        assertEquals("status_desc", DescNameResolver.resolve("status", camelCase = false))

    @Test
    fun `single char field with camelCase=true`() =
        assertEquals("xDesc", DescNameResolver.resolve("x", camelCase = true))

    @Test
    fun `single char field with camelCase=false`() =
        assertEquals("x_desc", DescNameResolver.resolve("x", camelCase = false))
}
