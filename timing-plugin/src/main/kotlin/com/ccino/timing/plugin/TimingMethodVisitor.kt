package com.ccino.timing.plugin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 * 自定义 MethodVisitor
 * 使用 AdviceAdapter 可以方便地在方法开始和结束处插入代码
 */
class TimingMethodVisitor(
    methodVisitor: MethodVisitor,
    access: Int,
    private val methodName: String,
    descriptor: String,
    private val className: String,
    private val annotationDescriptor: String,
    private val customLogTag: String?,
    private val minDuration: Long
) : AdviceAdapter(Opcodes.ASM9, methodVisitor, access, methodName, descriptor) {
    
    private var hasTiming = false
    private val startTimeVar = newLocal(Type.LONG_TYPE)
    private val startLabel = Label()
    private val endLabel = Label()
    
    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
        // 检查是否有指定的注解（支持自定义注解）
        if (descriptor == annotationDescriptor) {
            hasTiming = true
            println("[TimingPlugin] Found annotation $annotationDescriptor on $className.$methodName")
        }
        return super.visitAnnotation(descriptor, visible)
    }
    
    override fun onMethodEnter() {
        if (!hasTiming) return
        
        // 在方法开始处插入：
        // val startTime = System.currentTimeMillis()
        
        mv.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        mv.visitVarInsn(LSTORE, startTimeVar)
        
        // 标记 try 块开始
        mv.visitLabel(startLabel)
    }
    
    override fun onMethodExit(opcode: Int) {
        if (!hasTiming) return
        
        // 在方法正常退出前插入日志代码
        insertTimingLog()
    }
    
    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        if (hasTiming) {
            // 标记 try 块结束
            mv.visitLabel(endLabel)
            
            // 添加 finally 块
            val handlerLabel = Label()
            mv.visitLabel(handlerLabel)
            
            // 在 finally 块中也插入日志
            insertTimingLog()
            
            // 重新抛出异常
            mv.visitInsn(ATHROW)
            
            // 注册异常处理器
            mv.visitTryCatchBlock(startLabel, endLabel, handlerLabel, null)
        }
        
        super.visitMaxs(maxStack, maxLocals)
    }
    
    /**
     * 插入计时日志代码
     * Log.d(tag, "methodName: consume = duration ms")
     */
    private fun insertTimingLog() {
        // 获取简单类名（去掉包名）
        val simpleClassName = className.substringAfterLast('/')
        val logTag = customLogTag ?: simpleClassName
        
        // 计算耗时：duration = System.currentTimeMillis() - startTime
        mv.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/System",
            "currentTimeMillis",
            "()J",
            false
        )
        mv.visitVarInsn(LLOAD, startTimeVar)
        mv.visitInsn(LSUB)
        val durationVar = newLocal(Type.LONG_TYPE)
        mv.visitVarInsn(LSTORE, durationVar)
        
        // 如果设置了最小耗时阈值，添加判断
        if (minDuration > 0) {
            val skipLabel = Label()
            
            // if (duration < minDuration) skip
            mv.visitVarInsn(LLOAD, durationVar)
            mv.visitLdcInsn(minDuration)
            mv.visitInsn(LCMP)
            mv.visitJumpInsn(IFLT, skipLabel)
            
            // 插入日志代码
            insertLogCode(logTag, durationVar)
            
            mv.visitLabel(skipLabel)
        } else {
            // 直接插入日志代码
            insertLogCode(logTag, durationVar)
        }
    }
    
    /**
     * 插入实际的日志代码
     */
    private fun insertLogCode(logTag: String, durationVar: Int) {
        // Log.d(tag, "methodName: consume = duration ms")
        mv.visitLdcInsn(logTag)
        
        // 构建日志消息
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder")
        mv.visitInsn(DUP)
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false)
        
        mv.visitLdcInsn("$methodName: consume = ")
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )
        
        mv.visitVarInsn(LLOAD, durationVar)
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(J)Ljava/lang/StringBuilder;",
            false
        )
        
        mv.visitLdcInsn(" ms")
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "append",
            "(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            false
        )
        
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/StringBuilder",
            "toString",
            "()Ljava/lang/String;",
            false
        )
        
        // 调用 Log.d
        mv.visitMethodInsn(
            INVOKESTATIC,
            "android/util/Log",
            "d",
            "(Ljava/lang/String;Ljava/lang/String;)I",
            false
        )
        mv.visitInsn(POP) // 丢弃 Log.d 的返回值
    }
}
