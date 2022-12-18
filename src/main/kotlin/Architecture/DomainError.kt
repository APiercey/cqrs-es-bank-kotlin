package Architecture

class DomainError(message: String, val correlationId: String): Exception(message)