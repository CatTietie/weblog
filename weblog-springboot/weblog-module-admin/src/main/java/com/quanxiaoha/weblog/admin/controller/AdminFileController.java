package com.quanxiaoha.weblog.admin.controller;

import com.quanxiaoha.weblog.admin.service.AdminFileService;
import com.quanxiaoha.weblog.common.aspect.ApiOperationLog;
import com.quanxiaoha.weblog.common.utils.PageResponse;
import com.quanxiaoha.weblog.common.utils.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
@Api(tags = "Admin 文件模块")
public class AdminFileController {

    @Autowired
    private AdminFileService fileService;

    @PostMapping("/file/upload")
    @ApiOperation(value = "文件上传")
    @ApiOperationLog(description = "文件上传")
    @PreAuthorize("hasAuthority('file:upload')")
    public Response uploadFile(@RequestParam MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @PostMapping("/file/list")
    @ApiOperation(value = "文件列表")
    @ApiOperationLog(description = "查询文件列表")
    public PageResponse findFilePageList(@RequestParam(defaultValue = "1") Long current,
                                         @RequestParam(defaultValue = "10") Long size) {
        return fileService.findFilePageList(current, size);
    }

    @PostMapping("/file/delete")
    @ApiOperation(value = "删除文件")
    @ApiOperationLog(description = "删除文件")
    public Response deleteFile(@RequestParam Long id) {
        return fileService.deleteFile(id);
    }
}
