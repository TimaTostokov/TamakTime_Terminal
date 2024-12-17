package com.pos_terminal.tamaktime_temirnal.common

import retrofit2.Response

    abstract class BaseDataSource {

        protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
            return try {
                val response = call()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Resource.success(body)
                } else {
                    handleErrorResponse(response)
                }
            } catch (e: Exception) {
                error(e.message ?: e.toString())
            }
        }

        private fun <T> handleErrorResponse(response: Response<*>): Resource<T> {
            val message = when (response.code()) {
                401 -> buildString {
                    append("Invalid login or password")
                }
                403 -> buildString {
                    append(" You do not have permission to perform this operation")
                }
                404 -> buildString {
                    append("Server not found")
                    append(response.message())
                }
                500 -> buildString {
                    append("Server error")
                }
                else -> "${response.code()} ${response.message()}"
            }
            return error(message)
        }
        private fun <T> error(message: String): Resource<T> {
            return Resource.error("Error: $message")
        }

    }