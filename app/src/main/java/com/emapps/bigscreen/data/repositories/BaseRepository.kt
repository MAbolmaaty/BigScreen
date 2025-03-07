package com.emapps.bigscreen.data.repositories

import android.util.Log
import com.emapps.bigscreen.data.network.NetworkResponse
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class BaseRepository {

    companion object {
        private val TAG = BaseRepository::class.simpleName
    }

    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): NetworkResponse<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<T> = apiToBeCalled()
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    NetworkResponse.Success(responseBody)
                } else {
                    NetworkResponse.Failure(error = response.errorBody().toString())
                }
            } catch (e: HttpException) {
                NetworkResponse.Failure(error = "")
            } catch (e: IOException) {
                NetworkResponse.Failure(error = "")
            } catch (e: JsonDataException) {
                NetworkResponse.Failure(error = "")
            }  catch (e: Exception) {
                NetworkResponse.Failure(error = "")
            }
        }
    }
}