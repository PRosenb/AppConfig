package ch.pete.appconfig.exampleapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.pete.appconfig.AppConfig
import ch.pete.appconfig.AuthorizedApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // AppConfigApp from
        // https://github.com/PRosenb/AppConfigApp/releases
        AppConfig.authorizedApps.add(
            AuthorizedApp(
                applicationId = "ch.pete.appconfigapp",
                signature =
                "C7:0D:90:86:6D:3F:D2:3D:4C:7C:45:64:75:B0:93:97:00:5A:1F:FD:37:FE:8B:B1:61:8C:CF:EB:80:27:19:EC"
            )
        )

        // AppConfigApp in debug mode
        AppConfig.authorizedApps.add(
            AuthorizedApp(
                applicationId = "ch.pete.appconfigapp",
                signature =
                "FA:8B:0D:B3:D4:77:A7:CF:7C:2C:D9:CE:DC:E0:A8:99:49:B0:59:53:61:D0:D5:40:21:A5:07:DA:E4:64:94:1D"
            )
        )
    }
}
