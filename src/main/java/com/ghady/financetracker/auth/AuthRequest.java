package com.ghady.financetracker.auth;

public record AuthRequest(String name, String email, String password) { }