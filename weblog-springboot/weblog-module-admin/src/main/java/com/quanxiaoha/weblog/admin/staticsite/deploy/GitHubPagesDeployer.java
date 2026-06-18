package com.quanxiaoha.weblog.admin.staticsite.deploy;

import com.quanxiaoha.weblog.common.domain.dos.StaticSiteConfigDO;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

@Component
@Slf4j
public class GitHubPagesDeployer implements DeployStrategy {

    @Override
    public void deploy(String outputDir, StaticSiteConfigDO config) throws Exception {
        if (!Boolean.TRUE.equals(config.getGithubEnabled())) {
            return;
        }

        String token = config.getGithubToken();
        String repo = config.getGithubRepo();
        String branch = config.getGithubBranch() != null ? config.getGithubBranch() : "gh-pages";
        String cname = config.getGithubCname();

        String remoteUrl = "https://" + token + "@github.com/" + repo + ".git";

        Path tempDir = Files.createTempDirectory("weblog-gh-deploy");
        try {
            // 初始化临时 Git 仓库
            Git git = Git.init().setDirectory(tempDir.toFile()).call();

            // 复制输出目录到临时仓库
            Path source = Paths.get(outputDir);
            copyDirectory(source, tempDir);

            // 添加 CNAME 文件
            if (cname != null && !cname.isEmpty()) {
                Files.write(tempDir.resolve("CNAME"), cname.getBytes(StandardCharsets.UTF_8));
            }

            // 添加 .nojekyll 文件（防止 GitHub Pages 处理下划线文件）
            Files.write(tempDir.resolve(".nojekyll"), new byte[0]);

            // Git add all
            git.add().addFilepattern(".").call();

            // Git commit
            git.commit()
                    .setMessage("Deploy static site - " + java.time.LocalDateTime.now())
                    .setAuthor("weblog-bot", "weblog@noreply.com")
                    .call();

            // 重命名分支
            git.branchRename().setNewName(branch).call();

            // Push
            CredentialsProvider credentials = new UsernamePasswordCredentialsProvider(token, "");
            git.remoteAdd().setName("origin").setUri(new org.eclipse.jgit.transport.URIish(remoteUrl)).call();

            Iterable<PushResult> pushResults = git.push()
                    .setRemote("origin")
                    .setCredentialsProvider(credentials)
                    .setForce(true)
                    .call();

            // 解析推送结果，检测失败
            for (PushResult pushResult : pushResults) {
                for (RemoteRefUpdate refUpdate : pushResult.getRemoteUpdates()) {
                    RemoteRefUpdate.Status status = refUpdate.getStatus();
                    if (status != RemoteRefUpdate.Status.OK && status != RemoteRefUpdate.Status.UP_TO_DATE) {
                        String errorMsg = String.format("GitHub Push 失败: ref=%s, status=%s, message=%s",
                                refUpdate.getRemoteName(), status, refUpdate.getMessage());
                        git.close();
                        throw new RuntimeException(errorMsg);
                    }
                }
            }

            git.close();
            log.info("GitHub Pages 部署成功: {}/{}", repo, branch);

        } finally {
            deleteDirectory(tempDir);
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.warn("清理临时文件失败: {}", path);
                    }
                });
    }
}
