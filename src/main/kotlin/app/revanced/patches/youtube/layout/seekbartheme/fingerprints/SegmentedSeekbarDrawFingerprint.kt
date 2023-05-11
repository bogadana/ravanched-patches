package app.revanced.patches.youtube.layout.seekbartheme.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object SegmentedSeekbarDrawFingerprint : MethodFingerprint(
    "V", AccessFlags.PUBLIC or AccessFlags.STATIC, listOf("L", "L", "L", "L", "L", "L", "I", "Z"), listOf(
        Opcode.INT_TO_FLOAT,
        Opcode.INT_TO_FLOAT,
        Opcode.DIV_FLOAT_2ADDR,
        Opcode.ADD_FLOAT_2ADDR,
    )
)
