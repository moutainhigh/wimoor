package com.wimoor.amazon.auth.service.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.amazon.spapi.SellingPartnerAPIAA.AWSAuthenticationCredentials;
import com.amazon.spapi.SellingPartnerAPIAA.AWSAuthenticationCredentialsProvider;
import com.amazon.spapi.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import com.amazon.spapi.SellingPartnerAPIAA.ScopeConstants;
import com.amazon.spapi.api.AuthorizationApi;
import com.amazon.spapi.api.CatalogApi;
import com.amazon.spapi.api.FbaInboundApi;
import com.amazon.spapi.api.FeedsApi;
import com.amazon.spapi.api.FinancesApi;
import com.amazon.spapi.api.ListingsApi;
import com.amazon.spapi.api.NotificationsApi;
import com.amazon.spapi.api.OrdersV0Api;
import com.amazon.spapi.api.ReportsApi;
import com.amazon.spapi.api.SellersApi;
import com.amazon.spapi.api.TokensApi;
import com.wimoor.amazon.auth.pojo.entity.AmazonAuthority;

import cn.hutool.core.lang.Assert;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "auth")
public class ApiBuildService implements InitializingBean {

	@Setter
	private String appid;
	
    @Setter
    private String accessKeyId;
    
    @Setter
    private String secretKey;
    
    @Setter
    private String roleArn;
    
    @Setter
    private String clientId;
    
    @Setter
    private String clientSecret;

    @Setter
    private String sandbox;
    
    @Override
    public void afterPropertiesSet() {
        Assert.notBlank(accessKeyId, "AWSAuthenticationCredentials accessKeyId 为空");
        Assert.notBlank(secretKey, "AWSAuthenticationCredentials secretKey为空");
        Assert.notBlank(roleArn, "AWSAuthenticationCredentialsProvider roleArn为空");
        Assert.notBlank(clientId, "LWAAuthorizationCredentials clientId为空");
        Assert.notBlank(clientSecret, "LWAAuthorizationCredentials clientSecret为空");
 
    }
    
	public String getAccessKeyId() {
		return accessKeyId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getRoleArn() {
		return roleArn;
	}

	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public String getAppid() {
		return appid;
	}

	public String getSandbox() {
		return sandbox;
	}
	
	public AWSAuthenticationCredentials getAwsAuthenticationCredentials(AmazonAuthority auth) {
		   AWSAuthenticationCredentials awsAuthenticationCredentials=AWSAuthenticationCredentials.builder()
	             //IAM user的accessKeyId
	             .accessKeyId(accessKeyId)
	             //IAM user的secretKey
	             .secretKey(secretKey)
	             //这里按照amazon对不同region的分区填写，例子是北美地区的
	             .region(auth.getAWSRegion())
	             .build();
		 return awsAuthenticationCredentials;
	}

	 
	public AWSAuthenticationCredentialsProvider getAWSAuthenticationCredentialsProvider() {
	     AWSAuthenticationCredentialsProvider awsAuthenticationCredentialsProvider=AWSAuthenticationCredentialsProvider.builder()
	             //IAM role，特别注意：最好用IAM role当做IAM ARN去申请app
	              // 而且IAM user需要添加内联策略STS关联上IAM role，具体操作看：https://www.spapi.org.cn/cn/model2/_2_console.html
	              .roleArn(roleArn)
	              .roleSessionName("myrolesessioname121231313")
	              .build();
	     return awsAuthenticationCredentialsProvider;
	}

	public LWAAuthorizationCredentials getLWAAuthorizationCredentials(AmazonAuthority auth) {
	       LWAAuthorizationCredentials lwaAuthorizationCredentials = LWAAuthorizationCredentials.builder()
	             //申请app后LWA中的clientId
	             .clientId(clientId)
	             //申请app后LWA中的clientSecret
	             .clientSecret(clientSecret)
	             //店铺授权时产生的refreshToken或者app自授权生成的
	             .refreshToken(auth.getRefreshToken())
	             .endpoint("https://api.amazon.com/auth/o2/token")
	             .build();
	       return lwaAuthorizationCredentials;
	}
	
	public LWAAuthorizationCredentials getLWAAuthorizationCredentialsWithScope(AmazonAuthority auth,String scope) {
	       LWAAuthorizationCredentials lwaAuthorizationCredentials = LWAAuthorizationCredentials.builder()
	             //申请app后LWA中的clientId
	             .clientId(clientId)
	             //申请app后LWA中的clientSecret
	             .clientSecret(clientSecret)
	             .withScope(scope)
	             //店铺授权时产生的refreshToken或者app自授权生成的
	             .endpoint("https://api.amazon.com/auth/o2/token")
	             .build();
	       return lwaAuthorizationCredentials;
	}
	
	private String getEndPoint(String region) {
		String endpoint="";
		sandbox="false";
		if(sandbox!=null&&"true".equals(sandbox)) {
			if("us-east-1".equals(region)) {
				endpoint="https://sandbox.sellingpartnerapi-na.amazon.com";
			}else if("eu-west-1".equals(region)) {
				endpoint="https://sandbox.sellingpartnerapi-eu.amazon.com";
			}else if("us-west-2".equals(region)){
				endpoint="https://sandbox.sellingpartnerapi-fe.amazon.com";
			}
		}else {
			if("us-east-1".equals(region)) {
				endpoint="https://sellingpartnerapi-na.amazon.com";
			}else if("eu-west-1".equals(region)) {
				endpoint="https://sellingpartnerapi-eu.amazon.com";
			}else if("us-west-2".equals(region)){
				endpoint="https://sellingpartnerapi-fe.amazon.com";
			}
		}
		
		return endpoint;
	}
	
	public ReportsApi  getReportsApi(AmazonAuthority auth) {
	                     ReportsApi api = new ReportsApi.Builder()
			                 .awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
			                 .lwaAuthorizationCredentials(getLWAAuthorizationCredentials(auth))
			                 .awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
			                 .endpoint(getEndPoint(auth.getAWSRegion()))
			                 .build();
	                     return api;
	     }
	
	
	public ListingsApi  getProductApi(AmazonAuthority auth) {
		           ListingsApi api = new ListingsApi.Builder()
			            .awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
			            .lwaAuthorizationCredentials(getLWAAuthorizationCredentials(auth))
			            .awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
			            .endpoint(getEndPoint(auth.getAWSRegion()))
			            .build();
			        return api;
    }
	
	public OrdersV0Api  getOrdersV0Api(AmazonAuthority auth) {
		OrdersV0Api api = new OrdersV0Api.Builder()
            .awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
            .lwaAuthorizationCredentials(getLWAAuthorizationCredentials(auth))
            .awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
            .endpoint(getEndPoint(auth.getAWSRegion()))
            .build();
        return api;
	}
	
	public FeedsApi  getFeedApi(AmazonAuthority auth) {
		  FeedsApi api = new FeedsApi.Builder()
            .awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
            .lwaAuthorizationCredentials(getLWAAuthorizationCredentials(auth))
            .awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
            .endpoint(getEndPoint(auth.getAWSRegion()))
            .build();
        return api;
	}
	
	public FbaInboundApi  getInboundApi(AmazonAuthority auth) {
		   FbaInboundApi api = new FbaInboundApi.Builder()
           .awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
           .lwaAuthorizationCredentials(getLWAAuthorizationCredentials(auth))
           .awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
           .endpoint(getEndPoint(auth.getAWSRegion()))
           .build();
          return api;
	}
	
	public AuthorizationApi  getAuthorizationApi(AmazonAuthority auth) {
		AuthorizationApi api=new AuthorizationApi.Builder()
				.awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
				.awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
				.lwaAuthorizationCredentials(getLWAAuthorizationCredentialsWithScope(auth,ScopeConstants.SCOPE_MIGRATION_API))
		        .endpoint(getEndPoint(auth.getAWSRegion()))
				.build();
        return api;
	}

	public CatalogApi  getCatalogApi(AmazonAuthority auth) {
		CatalogApi api=new CatalogApi.Builder()
				.awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
				.awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
				.lwaAuthorizationCredentials(getLWAAuthorizationCredentials(auth))
		        .endpoint(getEndPoint(auth.getAWSRegion()))
				.build();
        return api;
	}
	
	public NotificationsApi getNotificationsApi(AmazonAuthority auth) {
		      NotificationsApi api=new NotificationsApi.Builder()
				.awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
				.awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
				.lwaAuthorizationCredentials(getLWAAuthorizationCredentialsWithScope(auth,ScopeConstants.SCOPE_NOTIFICATIONS_API))
		        .endpoint(getEndPoint(auth.getAWSRegion()))
				.build();
        return api;
	}
	
	public FinancesApi getFinancesApi(AmazonAuthority auth) {
			FinancesApi api=new FinancesApi.Builder()
				.awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
				.awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
				.lwaAuthorizationCredentials(getLWAAuthorizationCredentials(auth))
		        .endpoint(getEndPoint(auth.getAWSRegion()))
				.build();
		  return api;
	 }
	
	public TokensApi getTokensApi(AmazonAuthority auth) {
		// TODO Auto-generated method stub
		TokensApi api=new TokensApi.Builder()
				.awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
				.awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
				.lwaAuthorizationCredentials(getLWAAuthorizationCredentials(auth))
		        .endpoint(getEndPoint(auth.getAWSRegion()))
				.build();
        return api;
	}
	
	public SellersApi getSellersApi(AmazonAuthority auth){
		// TODO Auto-generated method stub
		SellersApi api=new SellersApi.Builder()
				.awsAuthenticationCredentials(getAwsAuthenticationCredentials(auth))
				.awsAuthenticationCredentialsProvider(getAWSAuthenticationCredentialsProvider())
				.lwaAuthorizationCredentials(getLWAAuthorizationCredentials(auth))
		        .endpoint(getEndPoint(auth.getAWSRegion()))
				.build();
        return api;
	}


}
