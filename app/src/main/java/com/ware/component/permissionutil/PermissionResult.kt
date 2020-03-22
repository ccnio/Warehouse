package com.ware.component.permissionutil

/**
 * Created by jianfeng.li on 2020/3/22.
 */
sealed class PermissionResult {
    object Grant : PermissionResult()
    class Deny(val permissions: Array<String>) : PermissionResult()
    class Rationale(val permissions: Array<String>) : PermissionResult()
}