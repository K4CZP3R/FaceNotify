package xyz.k4czp3r.facenotify.models

data class UnlockType (val name: String, val detectLine: String, val successContains: String, val id: Int)


val AOSPA = UnlockType("AOSPA", "onAuthenticated", "(true)",0)
val ONEPLUS = UnlockType("OxygenOS", "FacelockTrust", ",true",1)
val MIUI = UnlockType("MIUI", "FaceAuthManager: compare", "201",2)
val LG = UnlockType("LG", "recognizing result: ", "user_main",3)
val SAMSUNG_OLD_FACE = UnlockType("Samsung (Old, Face)", "SemBioFaceServiceD: handleAuthenticated :",": 1",4)
val SAMSUNG_OLD_IRIS = UnlockType("Samsung (Old, Iris)", "IrisService: handleAuthenticated :",": true",5)
val SAMSUNG_OLD_INTE = UnlockType("Samsung (Old, Intelligent)", "IBS_BiometricsService: handleAuthenticated :",": 2",6)
val SAMSUNG_FACE = UnlockType("Samsung (New, Face)", "BiometricServiceBase: handleAuthenticated: ","true",7)



val UnlockTypes = listOf<UnlockType>(AOSPA, ONEPLUS, MIUI, LG, SAMSUNG_OLD_FACE, SAMSUNG_OLD_INTE, SAMSUNG_OLD_IRIS, SAMSUNG_FACE)