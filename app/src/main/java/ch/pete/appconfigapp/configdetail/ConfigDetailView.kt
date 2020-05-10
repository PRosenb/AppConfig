package ch.pete.appconfigapp.configdetail

interface ConfigDetailView {
    fun showKeyValueDetails(configId: Long, keyValueId: Long?)
}
