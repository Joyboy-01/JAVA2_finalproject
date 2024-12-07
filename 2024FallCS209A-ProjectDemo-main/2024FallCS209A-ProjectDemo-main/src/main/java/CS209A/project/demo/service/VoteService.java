package CS209A.project.demo.service;

import CS209A.project.demo.entity.Vote;
import CS209A.project.demo.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import CS209A.project.demo.entity.User;


@Service
public class VoteService {
    @Autowired
    private VoteRepository voteRepository;

//    public VoteService(VoteRepository repo) {
//        this.repo = repo;
//    }
//    public List<Vote> findAll(){return repo.findAll();}
//    public Vote save(Vote v){return repo.save(v);}
//    public Vote findById(Long id){return repo.findById(id).orElse(null);}
//    public void delete(Long id){repo.deleteById(id);}

    public List<Vote> getHighReputationVotes(List<User> highReputationUsers) {
        // 将高声誉用户的 userId 提取到一个 Set 中
        Set<Long> highReputationUserIds = highReputationUsers.stream()
                .map(User::getExternalUserId)  // 提取每个高声誉用户的 userId
                .collect(Collectors.toSet());  // 将所有 userId 收集到一个 Set 中

        // 从数据库中获取所有投票记录，并过滤出属于高声誉用户的投票
        return voteRepository.findAll().stream()
                .filter(vote -> highReputationUserIds.contains(vote.getUserId()))  // 过滤：如果投票的用户在高声誉用户中，则保留
                .collect(Collectors.toList());  // 将符合条件的投票记录收集到一个 List 中返回
    }


}