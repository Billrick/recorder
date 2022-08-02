package com.rick.framework.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class RepositoryConfig {
    @Value("${repository.user}")
    private String user;
    @Value("${repository.repository}")
    private String repository;
    @Value("${repository.access-token}")
    private String accessToken;
    @Value("${repository.type}")
    private String repositoryType;

    private String giteeOf = "https://gitee.com/api/v5/repos/{0}/{1}/contents/{2}/{3}";
    private String githubOf = "https://api.github.com/repos/{0}/{1}/contents/{2}/{3}";

    public static final String REPOSITORY_TYPE_GITEE = "gitee";
    public static final String REPOSITORY_TYPE_GITHUB = "github";

    public boolean isGithub(){
        if(REPOSITORY_TYPE_GITHUB.equals(repositoryType)){
            return true;
        }
        return false;
    }

    public String getOpFileUrl (){
        if(REPOSITORY_TYPE_GITEE.equals(repositoryType)){
            return giteeOf;
        }else if(REPOSITORY_TYPE_GITHUB.equals(repositoryType)){
            return githubOf;
        }
        return null;
    }


}
