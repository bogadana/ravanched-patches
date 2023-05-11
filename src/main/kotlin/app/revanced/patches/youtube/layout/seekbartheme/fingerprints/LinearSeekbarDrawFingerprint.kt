package app.revanced.patches.youtube.layout.seekbartheme.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.Opcode

object LinearSeekbarDrawFingerprint : MethodFingerprint(
    "V", AccessFlags.PUBLIC or AccessFlags.FINAL, listOf("L"), listOf(
        Opcode.INT_TO_FLOAT,
        Opcode.MUL_FLOAT,
        Opcode.FLOAT_TO_INT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.GOTO
    )
)
