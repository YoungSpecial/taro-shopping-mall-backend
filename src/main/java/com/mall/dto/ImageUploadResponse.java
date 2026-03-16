package com.mall.dto;

public record ImageUploadResponse(
        String imageUrl,
        String fileName,
        long fileSize
) {
}