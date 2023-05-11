package app.revanced.patches.youtube.layout.seekbartheme.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.annotation.Version
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.addInstruction
import app.revanced.patcher.extensions.addInstructions
import app.revanced.patcher.extensions.removeInstruction
import app.revanced.patcher.extensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.seekbartheme.annotations.SeekbarThemeCompatibility
import app.revanced.patches.youtube.layout.seekbartheme.fingerprints.LinearSeekbarDrawFingerprint
import app.revanced.patches.youtube.layout.seekbartheme.fingerprints.SegmentedSeekbarDrawFingerprint
import org.jf.dexlib2.iface.instruction.ReferenceInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction35c
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc
import org.jf.dexlib2.builder.BuilderInstruction

@Patch
@Name("seekbar-theme")
@Description("Applies a custom theme to the seekbar.")
@SeekbarThemeCompatibility
@Version("0.0.1")
class SeekbarThemePatch : BytecodePatch(
    listOf(
        SegmentedSeekbarDrawFingerprint, LinearSeekbarDrawFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        val segmentedResult = SegmentedSeekbarDrawFingerprint.result!!
        val segmentedMethod = segmentedResult.mutableMethod
        val segmentedDrawCalls = segmentedMethod.implementation?.instructions?.withIndex()?.filter { (it.value as? ReferenceInstruction)?.reference.toString().contains("drawRect") }
        val segmentedReplacements = listOf(0 to "splitSeekbarActiveSegmentDrawHook", 1 to "splitSeekbarInactiveSegmentDrawHook") 

        segmentedReplacements.forEach {  
            val (drawInstIndex, drawInst) = segmentedDrawCalls!!.get(it.first)
            val drawCall = drawInst as Instruction3rc

            segmentedMethod.replaceInstruction(drawInstIndex,
                """
                    invoke-static/range { v${drawCall.startRegister} .. v${drawCall.registerCount + 1} }, $INTEGRATIONS_CLASS_DESCRIPTOR->${it.second}(Landroid/graphics/Canvas;FFFFLandroid/graphics/Paint;)V 
                """.trimIndent()
            )   
        }
        
        val linearResult = LinearSeekbarDrawFingerprint.result!!
        val linearMethod = linearResult.mutableMethod
        val linearDrawCalls = linearMethod.implementation?.instructions?.withIndex()?.filter { (it.value as? ReferenceInstruction)?.reference.toString().contains("drawRect") }

        val linearReplacements = listOf(1 to "linearSeekbarUnbufferedDrawHook", 7 to "linearSeekbarInactiveBufferedDrawHook", 8 to "linearSeekbarActiveBufferedDrawHook", 5 to "linearSeekbarInactivePrimaryDrawHook", 6 to "linearSeekbarPrimaryDrawHook")

        linearReplacements.forEach { 
            val (drawInstIndex, drawInst) = linearDrawCalls!!.get(it.first)
            val linearDrawCall = drawInst as Instruction35c;

            linearMethod.replaceInstruction(drawInstIndex,
                """
                    invoke-static { v${linearDrawCall.registerC}, v${linearDrawCall.registerD}, v${linearDrawCall.registerE} }, $INTEGRATIONS_CLASS_DESCRIPTOR->${it.second}(Landroid/graphics/Canvas;Landroid/graphics/Rect;Landroid/graphics/Paint;)V 
                """.trimIndent()
            )
        }

        return PatchResultSuccess()
    }

    private companion object {
        private const val INTEGRATIONS_CLASS_DESCRIPTOR = "Lapp/revanced/integrations/patches/theme/ThemePatch;"
    }
}
