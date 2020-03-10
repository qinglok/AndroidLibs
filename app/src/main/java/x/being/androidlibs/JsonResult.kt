package x.being.androidlibs

data class JsonResult<T>(var status: Status? = null, var message: String? = null, var data: T? = null)