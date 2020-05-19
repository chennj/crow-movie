package org.crow.movie.user.common.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public final class CC {

	public static final List<Map<String, Object>> FEEDBACK_TYPE;
	
	
	static {
		FEEDBACK_TYPE = new ArrayList<Map<String, Object>>(){
			{
				this.add(new HashMap<String, Object>(){{
					this.put("id", "1");
					this.put("title", "无法播放");
				}});
				this.add(new HashMap<String, Object>(){{
					this.put("id", "2");
					this.put("title", "播放卡顿");
				}});
				this.add(new HashMap<String, Object>(){{
					this.put("id", "3");
					this.put("title", "标签错误");
				}});
				this.add(new HashMap<String, Object>(){{
					this.put("id", "4");
					this.put("title", "分类错误");
				}});
				this.add(new HashMap<String, Object>(){{
					this.put("id", "5");
					this.put("title", "搜索不准");
				}});
				this.add(new HashMap<String, Object>(){{
					this.put("id", "6");
					this.put("title", "推荐不准");
				}});
				this.add(new HashMap<String, Object>(){{
					this.put("id", "7");
					this.put("title", "无法下载");
				}});
				this.add(new HashMap<String, Object>(){{
					this.put("id", "8");
					this.put("title", "其他");
				}});
			}
		};
	}
}
