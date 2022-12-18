package Architecture

class DomainError(message: String, val corrolationId: String): Exception(message)