package x.being.androidlibs.model

data class JsonResult<T>(var status: Status? = null, var message: String? = null, var data: T? = null)