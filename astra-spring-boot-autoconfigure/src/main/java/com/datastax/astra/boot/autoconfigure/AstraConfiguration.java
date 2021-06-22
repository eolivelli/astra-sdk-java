/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.astra.boot.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraClient.AstraClientBuilder;
import com.datastax.oss.driver.api.core.CqlSession;

/**
 * Initializing AstraClient (if class present in classpath)
 * - #1 Configuration with application.properties
 * - #2 Configuration with environment variables
 * - #3 Configuration with AstraRC on file system in user.home
 * 
 * You can also define your {@link AstraClient} explicitely.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Configuration
@ConditionalOnClass(AstraClient.class)
@EnableConfigurationProperties(AstraClientProperties.class)
public class AstraConfiguration {

    @Autowired
    private AstraClientProperties astraClientProperties;
    
    @Bean
    @ConditionalOnMissingBean
    public AstraClient astraClient() {
        /* 
         * Load properties and initialize the client
         */
        AstraClientBuilder builder = AstraClient.builder();
        
        if (null != astraClientProperties.getDatabaseId() &&
                !"".equals(astraClientProperties.getDatabaseId())) {
            builder = builder.databaseId(astraClientProperties.getDatabaseId());  
        }
        
        if (null != astraClientProperties.getCloudRegion() &&
                !"".equals(astraClientProperties.getCloudRegion())) {
            builder = builder.cloudProviderRegion(astraClientProperties.getCloudRegion());  
        }
        
        if (null != astraClientProperties.getApplicationToken() &&
                !"".equals(astraClientProperties.getApplicationToken())) {
            builder = builder.appToken(astraClientProperties.getApplicationToken());  
        }
        
        if (null != astraClientProperties.getClientId() &&
                !"".equals(astraClientProperties.getClientId())) {
            builder = builder.clientId(astraClientProperties.getClientId());  
        }
        
        if (null != astraClientProperties.getClientSecret() &&
                !"".equals(astraClientProperties.getClientSecret())) {
            builder = builder.clientSecret(astraClientProperties.getClientSecret());  
        }
        
        if (null != astraClientProperties.getSecureConnectBundlePath() &&
            !"".equals(astraClientProperties.getSecureConnectBundlePath())) {
            builder = builder.secureConnectBundle(astraClientProperties.getSecureConnectBundlePath());
        }
        
        if (null != astraClientProperties.getKeyspace() &&
                !"".equals(astraClientProperties.getKeyspace())) {
            builder = builder.keyspace(astraClientProperties.getKeyspace());  
        }
        
        return builder.build();
    }
    
    /**
     * We want the CqlSession generated by {@link AstraClient}.
     * 
     * @param astraClient
     *      astraClient
     * @return
     *      the ccassandra session
     */
    @Bean
    public CqlSession cqlSession(AstraClient astraClient) {
        return astraClient.cqlSession();
    }
    
    // Could be null if only token provided
    //@Bean
    //public StargateClient stargateClient(AstraClient astraClient) {
    //    return astraClient.getStargateClient();
    // }

}
